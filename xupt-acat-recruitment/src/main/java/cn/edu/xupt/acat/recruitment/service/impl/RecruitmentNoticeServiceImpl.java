package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import cn.edu.xupt.acat.recruitment.dao.RecruitmentDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbRecruitment;
import cn.edu.xupt.acat.recruitment.domain.vo.SendNoticeVo;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.service.RecruitmentNoticeService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service@PropertySource(value = {"classpath:email.properties"}, encoding = "UTF-8")
public class RecruitmentNoticeServiceImpl implements RecruitmentNoticeService {

    private static Logger logger = Logger.getLogger(RecruitmentNoticeServiceImpl.class.toString());

    @Resource
    private RecruitmentDao recruitmentDao;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void notice(SendNoticeVo input) {
        getParam(input);
        QueryWrapper<TbRecruitment> wrapper = new QueryWrapper<>();
        wrapper.eq("nid", input.getNid());
        wrapper.eq("work_type", input.getWorkType());
        List<TbRecruitment> recruitments = recruitmentDao.selectList(wrapper);
        if (recruitments == null || recruitments.size() == 0) {
            logger.warning("there is no recruitment info with nid = " + input.getNid() + " & workType = " + input.getWorkType());
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        //send email
        doSendNotice(input);
        //update status
        TbRecruitment recruitment = recruitments.get(0);
        recruitment.setNoticeStatus(1);
        recruitmentDao.updateById(recruitment);
    }

    /**
     * 參數校驗
     *
     * @param input input
     */
    private void getParam(SendNoticeVo input) {

        if (input == null) {
            logger.warning("recruitment info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getNid())) {
            logger.warning("recruitment nid is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getWorkType())) {
            logger.warning("recruitment work type is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getReceiveAddress())) {
            logger.warning("recruitment receive email address is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (input.getMonth() == null) {
            logger.warning("recruitment receive month is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        if (input.getDay() == null) {
            logger.warning("recruitment receive day is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        if (input.getStart() == null) {
            logger.warning("recruitment receive start time is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        if (input.getEnd() == null) {
            logger.warning("recruitment receive end time is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive recruitment send notice with data : " + JSON.toJSONString(input));
    }


    private void doSendNotice(SendNoticeVo input) {
        TbNotice notice = new TbNotice();
        notice.setNid(input.getNid());
        notice.setWorkType(input.getWorkType());
        notice.setReceiveUser(RecruitmentUtil.getUsername(input.getNid()));
        notice.setReceiveAddress(input.getReceiveAddress());
        notice.setSendUser(input.getOpUser());
        notice.setSendTime(RecruitmentUtil.getTime());
        notice.setTitle(EMAIL_INTERVIEW_BEFORE_NOTE_TITLE);
        String nickName = StringUtils.isEmpty(input.getRealName()) ? RecruitmentUtil.getUsername(input.getNid()) : input.getRealName();
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(notice.getWorkType());
        notice.setContent(getItwNoteContent(nickName,
                input.getMonth(), input.getDay(),
                input.getStart(), input.getEnd(),
                turns, RecruitmentUtil.getFormatTime()));
        kafkaTemplate.send(RecruitmentConstant.NOTICE_RECEIVE_TOPIC, JSON.toJSONString(notice));
        logger.info("send notice with data : " + JSON.toJSONString(notice));
    }

    @Value("${EMAIL_INTERVIEW_BEFORE_NOTE_TITLE}")
    private String EMAIL_INTERVIEW_BEFORE_NOTE_TITLE;

    @Value("${EMAIL_INTERVIEW_BEFORE_NOTE_CONTENT}")
    private String EMAIL_INTERVIEW_BEFORE_NOTE_CONTENT;

    private String getItwNoteContent(String name, int month, int day, int start, int end, int itwType, String time) {
        String content = EMAIL_INTERVIEW_BEFORE_NOTE_CONTENT;
        content = content.replace("{{name}}", name);
        content = content.replace("{{month}}", Integer.toString(month));
        content = content.replace("{{day}}", Integer.toString(day));
        content = content.replace("{{start}}", Integer.toString(start));
        content = content.replace("{{end}}", Integer.toString(end));
        content = content.replace("{{itw_type}}", Integer.toString(itwType));
        return content.replace("{{time}}", time);
    }
}
