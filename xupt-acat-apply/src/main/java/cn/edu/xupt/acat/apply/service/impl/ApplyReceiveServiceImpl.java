package cn.edu.xupt.acat.apply.service.impl;

import cn.edu.xupt.acat.apply.dao.ApplyDao;
import cn.edu.xupt.acat.apply.domain.entity.TbApply;
import cn.edu.xupt.acat.apply.library.ApplyCodeEnum;
import cn.edu.xupt.acat.apply.library.ApplyConstant;
import cn.edu.xupt.acat.apply.library.ApplyUtil;
import cn.edu.xupt.acat.apply.service.ApplyReceiveService;
import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowCallBackService;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service(async = true)
@Component
@PropertySource(value = {"classpath:email.properties"}, encoding = "UTF-8")
public class ApplyReceiveServiceImpl implements ApplyReceiveService {

    private static Logger logger = Logger.getLogger(ApplyReceiveServiceImpl.class.toString());
    @Resource
    private ApplyDao applyDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Reference
    private FlowCallBackService flowCallBackService;

    @Reference
    private FlowEsDumpService flowEsDumpService;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
//    @Transactional
    public R receive(FlowSendInput input) {
        getParam(input);
        //1.insert db & es
        doSaveInfo(input);
        //2.异步更新flow es
        doUpdateFlowEs(input);
        //3.send flow
        sendFlow(input);
        //4.send notice
        doSendNotice(input);
        return R.ok();
    }

    private void getParam(FlowSendInput input) {

        if (input == null || input.getApplyInfo() == null) {
            logger.warning("input or apply info is empty. " + JSON.toJSONString(input));
            ExceptionCast.exception(ApplyCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ApplyCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getApplyInfo().getNid())) {
            logger.warning("nid is empty. " + JSON.toJSONString(input));
            ExceptionCast.exception(ApplyCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), ApplyCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive apply info : " + JSON.toJSONString(input));
    }

    /**
     * 数据存储
     * @param input input
     */
    private TbApply doSaveInfo(FlowSendInput input) {
        TbApply apply = buildApplyCondition(input);
        applyDao.insert(apply);
        IndexQuery condition = buildEsCondition(apply);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbApply.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
        logger.info("insert db and es with apply info :" + JSON.toJSONString(apply));
        return apply;
    }

    /**
     * 构造Apply数据
     * @param input input
     * @return TbApply
     */
    private TbApply buildApplyCondition(FlowSendInput input) {
        TbApply apply = new TbApply();
        apply.setNid(input.getNid());
        apply.setUsername(input.getApplyInfo().getUsername());
        apply.setClassName(input.getApplyInfo().getClassName());
        apply.setContent(input.getApplyInfo().getContent());
        apply.setSnumber(input.getApplyInfo().getSnumber());
        apply.setPhone(input.getApplyInfo().getPhone());
        apply.setSex(input.getApplyInfo().getSex());
        apply.setRealName(input.getApplyInfo().getRealName());
        return apply;
    }

    private IndexQuery buildEsCondition(TbApply apply) {
        return  new IndexQueryBuilder()
                .withId(Long.toString(apply.getId()))
                .withObject(apply)
                .build();
    }

    //send flow
    private void sendFlow(FlowSendInput input) {
        FlowReceiveInput receive = ApplyUtil.getFlowReceiveInput(input);
        receive.setUsername(input.getApplyInfo().getUsername());
        receive.setModuleStatus(1);
        receive.setWorkType(ApplyConstant.WORK_NAME_APPLY);
        receive.setEmail(input.getEmail());
        flowCallBackService.callback(receive);
        logger.info("send flow with info : " + JSON.toJSONString(receive));
    }

    private void doUpdateFlowEs(FlowSendInput input) {
        FlowEsEntity esEntity = new FlowEsEntity();
        esEntity.setNid(input.getNid());
        esEntity.setWorkType(ApplyConstant.WORK_TYPE_APPLY);
        flowEsDumpService.dumpToEs(esEntity);
        logger.info("update flow es work type : " + esEntity.getWorkType());
    }


    private void doSendNotice(FlowSendInput input) {
        TbNotice notice = new TbNotice();
        notice.setNid(input.getNid());
        notice.setWorkType(input.getWorkType());
        notice.setReceiveUser(ApplyUtil.getUsername(input.getNid()));
        notice.setReceiveAddress(input.getEmail());
        notice.setSendUser(ApplyConstant.APPLY_OPERATION_USER);
        notice.setSendTime(ApplyUtil.getTime());
        notice.setTitle(EMAIL_APPLY_SUCCESS_TITLE);
        String nickName = StringUtils.isEmpty(input.getApplyInfo().getRealName()) ? ApplyUtil.getUsername(input.getNid()) : input.getApplyInfo().getRealName();
        notice.setContent(getApplyNoticeContent(nickName,ApplyUtil.getFormatTime()));
        kafkaTemplate.send(ApplyConstant.NOTICE_RECEIVE_TOPIC, JSON.toJSONString(notice));
        logger.info("send notice with data : " + JSON.toJSONString(notice));
    }

    @Value("${EMAIL_APPLY_SUCCESS_TITLE}")
    private String EMAIL_APPLY_SUCCESS_TITLE;

    @Value("${EMAIL_APPLY_SUCCESS_CONTENT}")
    private String EMAIL_APPLY_SUCCESS_CONTENT;

    private String getApplyNoticeContent(String name, String time) {
        String content = EMAIL_APPLY_SUCCESS_CONTENT;
        content = content.replace("{{name}}", name);
        content = content.replace("{{time}}", time);
        return content;
    }
}
