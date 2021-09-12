package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.logs.domain.TbLogs;
import cn.edu.xupt.acat.recruitment.dao.RecruitmentDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbRecruitment;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.service.RecruitmentReceiveService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service(async = true)
@Component
public class RecruitmentReceiveServiceImpl implements RecruitmentReceiveService {

    private static Logger logger = Logger.getLogger(RecruitmentReceiveServiceImpl.class.toString());

    @Resource
    private RecruitmentDao recruitmentDao;

    @Reference
    private FlowEsDumpService flowEsDumpService;

    @Reference
    private FlowSearchService flowSearchService;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public R receive(FlowSendInput input) {
        getParam(input);
        //1.insert db
        doSaveInfo(input);
        //2.update work type
        int turns = RecruitmentUtil.getTurns(input.getWorkType());
        RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, input.getNid(), RecruitmentConstant.WORK_TYPE_NOT_SIGN + "_" + turns);
        //3. write logs
        doWriteLogs(input);
        return R.ok();
    }

    /**
     * 参数校验
     * @param input input
     */
    private void getParam(FlowSendInput input) {

        if (input == null) {
            logger.warning(" input info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getNid())) {
            logger.warning(" nid info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getWorkType())) {
            logger.warning(" work type info is empty.");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive flow info with : " + JSON.toJSONString(input));
    }

    private void doSaveInfo(FlowSendInput input) {
        TbRecruitment recruitment = new TbRecruitment();
        recruitment.setUsername(RecruitmentUtil.getUsername(input.getNid()));
        recruitment.setCreateTime(RecruitmentUtil.getTime());
        recruitment.setNid(input.getNid());
        recruitment.setWorkType(input.getWorkType());
        recruitment.setNoticeStatus(0);
        recruitmentDao.insert(recruitment);
    }

    /**
     * 发送日志
     * @param input input
     */
    private void doWriteLogs(FlowSendInput input) {
        TbLogs logs = buildLogsCondition(input);
        kafkaTemplate.send(RecruitmentConstant.TOPIC_LOGS, JSON.toJSONString(logs));
        logger.info("send logs, topic is " + RecruitmentConstant.TOPIC_LOGS + ", and send log data : " + JSON.toJSONString(logs));
    }


    /**
     * 构造log数据
     * @param input input
     * @return TbLogs
     */
    private TbLogs buildLogsCondition(FlowSendInput input) {
        TbLogs logs = new TbLogs();
        logs.setNid(input.getNid());
        logs.setUsername(RecruitmentUtil.getUsername(input.getNid()));
        logs.setOpUser(RecruitmentConstant.RECRUITMENT_SYSTEM_OP_USER);
        logs.setOpTime(RecruitmentUtil.getTime());
        logs.setOpDetails("等待签到");
        int turns = input.getWorkType().split("_").length == 1 ? 1 : Integer.parseInt(input.getWorkType().split("_")[1]);
        logs.setWorkType(RecruitmentConstant.WORK_TYPE_NOT_SIGN + "_" + turns);
        logs.setOpExt(null);
        return logs;
    }
}
