package cn.edu.xupt.acat.user.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.util.BCryptUtil;
import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import cn.edu.xupt.acat.user.dao.UserDao;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.domain.vo.UserVo;
import cn.edu.xupt.acat.user.library.UserCodeEnum;
import cn.edu.xupt.acat.user.library.UserConstant;
import cn.edu.xupt.acat.user.library.UserUtil;
import cn.edu.xupt.acat.user.service.UserRegisterService;
import cn.edu.xupt.acat.user.service.UserSearchService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@PropertySource(value = {"classpath:email.properties"}, encoding = "UTF-8")
public class UserRegisterServiceImpl implements UserRegisterService {

    private static Logger logger = Logger.getLogger(UserRegisterServiceImpl.class.toString());

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private UserDao userDao;

    @Resource
    private UserSearchService userSearchService;

    private void getRegisterParam(UserVo input){
        if (!UserUtil.checkEmail(input.getEmail())){
            logger.warning("email is illegal :" + JSON.toJSONString(input));
            ExceptionCast.exception(UserCodeEnum.ILLEGAL_EMAIL_EXCEPTION.getMsg(), UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        if (StringUtils.isEmpty(input.getCode())) {
            logger.warning("check code is empty :" + JSON.toJSONString(input));
            ExceptionCast.exception(UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        if (StringUtils.isEmpty(input.getPassword())) {
            logger.warning("user password is empty :" + JSON.toJSONString(input));
            ExceptionCast.exception(UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        logger.info("getRegisterParam param :" + JSON.toJSONString(input));
    }

    @Override
    @Transactional
    public R register(UserVo input) {
        getRegisterParam(input);
        //校验check code
        String code = redisTemplate.opsForValue().get(UserConstant.USER_CHECK_CODE_REDIS_PREFIX + input.getEmail());
        if (!input.getCode().equals(code)){
            logger.info(input.getEmail() + " verification user check code fail. submit code is " + input.getCode() + ", but real code is " + code);
            return R.error(UserCodeEnum.USER_CHECK_CODE_ERROR.getCode(),UserCodeEnum.USER_CHECK_CODE_ERROR.getMsg());
        }
        Boolean res = redisTemplate.delete(UserConstant.USER_CHECK_CODE_REDIS_PREFIX + input.getEmail());
        if (res != null && res.booleanValue() == true) {
            logger.info(input.getEmail() + " verification user check code success, delete redis key: " + UserConstant.USER_CHECK_CODE_REDIS_PREFIX + input.getEmail());
        } else {
            logger.warning(input.getEmail() + " verification user check code success,but delete redis key fail: " + UserConstant.USER_CHECK_CODE_REDIS_PREFIX + input.getEmail());
        }
        doRegister(input);
        return R.ok();
    }

    private void getCheckCodeParam(UserVo input){
        if (!UserUtil.checkEmail(input.getEmail())){
            logger.warning("email is illegal :" + JSON.toJSONString(input));
            ExceptionCast.exception(UserCodeEnum.ILLEGAL_EMAIL_EXCEPTION.getMsg(), UserCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        input.setUsername(UserUtil.getUsername(input.getEmail()));
        logger.info("getCheckCode param :" + JSON.toJSONString(input));
    }

    /**
     * 数据存储
     * @param input
     */
    private void doRegister(UserVo input) {
        //生成User信息
        TbUser tbUser = new TbUser();
        String username = UserUtil.getUsername(input.getEmail());
        tbUser.setUsername(username);
        tbUser.setEmail(input.getEmail());
        tbUser.setPassword(BCryptUtil.encode(input.getPassword()));
        tbUser.setNickName(input.getNickName());
        tbUser.setUserPic(UserConstant.USER_INFO_DEFAULT_PIC);
        tbUser.setStatus(1);
        Date now = UserUtil.getTime();
        tbUser.setCreateTime(now);
        tbUser.setUpdateTime(now);
        //insert db & es
        logger.info("insert db and es user info:" + JSON.toJSONString(tbUser));
        userDao.insert(tbUser);
        IndexQuery condition = buildEsCondition(tbUser);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbUser.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
    }

    @Override
    public R getCheckCode(UserVo input) {
        getCheckCodeParam(input);
        if (checkExist(input)) {
            logger.warning("user info was created. repeat email : " + input.getEmail());
            return R.error(UserCodeEnum.DATA_EXISTED_EXCEPTION.getCode(), UserCodeEnum.DATA_EXISTED_EXCEPTION.getMsg());
        }
        String code = UserUtil.getCode();
        //设置过期时间
        redisTemplate.opsForValue().set(UserConstant.USER_CHECK_CODE_REDIS_PREFIX + input.getEmail(), code, 10, TimeUnit.MINUTES);
        logger.info("check code info is save to redis key-value:" + input.getEmail() + "-" + code);
        //异步通知
        doSendNotice(input, code);
        return R.ok();
    }

    /**
     * 构造ES插入数据
     * @param user
     * @return
     */
    private IndexQuery buildEsCondition(TbUser user){
        return  new IndexQueryBuilder()
                .withId(Long.toString(user.getId()))
                .withObject(user)
                .build();
    }

    /**
     * 检查重复
     * @param vo
     * @return
     */
    private boolean checkExist(UserVo vo) {
        TbUser user = new TbUser();
        user.setEmail(vo.getEmail());
        List<TbUser> users = userSearchService.query(user);
        if (users != null && users.size() > 0) {
            return true;
        }
        return false;
    }

    private void doSendNotice(UserVo input, String code) {
        TbNotice notice = new TbNotice();
        notice.setReceiveUser(input.getUsername());
        notice.setSendUser("user_system");
        notice.setReceiveAddress(input.getEmail());
        notice.setTitle(EMAIL_BINDING_CHECK_CODE_TITLE);
        notice.setContent(getCheckCodeEmailContent(input.getUsername(), code, UserUtil.getFormatTime()));
        kafkaTemplate.send(UserConstant.NOTICE_RECEIVE_TOPIC, JSON.toJSONString(notice));
        logger.info("send notice with data : " + JSON.toJSONString(notice));
    }

    @Value("${EMAIL_BINDING_CHECK_CODE_TITLE}")
    private String EMAIL_BINDING_CHECK_CODE_TITLE;

    @Value("${EMAIL_BINDING_CHECK_CODE_CONTENT}")
    private String EMAIL_BINDING_CHECK_CODE_CONTENT;

    private String getCheckCodeEmailContent(String name, String code, String time) {
        String content = EMAIL_BINDING_CHECK_CODE_CONTENT;
        content = content.replace("{{name}}", name);
        content = content.replace("{{code}}", code);
        return content.replace("{{time}}", time);
    }
}
