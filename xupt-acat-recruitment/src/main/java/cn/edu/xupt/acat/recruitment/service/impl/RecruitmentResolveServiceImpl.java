package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowCallBackService;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.logs.domain.TbLogs;
import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.library.RedisLock;
import cn.edu.xupt.acat.recruitment.service.RecruitmentResolveService;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;


@Service
@PropertySource(value = {"classpath:email.properties"}, encoding = "UTF-8")
public class RecruitmentResolveServiceImpl implements RecruitmentResolveService {

    private static Logger logger = Logger.getLogger(RecruitmentResolveServiceImpl.class.toString());

    @Reference
    private FlowSearchService flowSearchService;

    @Reference
    private ServiceLineSearchService serviceLineSearchService;

    @Reference
    private FlowEsDumpService flowEsDumpService;

    @Reference
    private FlowCallBackService flowCallBackService;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public R resolve(String nid, int status, String opUser) {
        //1.查询数据
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, nid);
        if (esEntity == null) {
            logger.warning("flow es doesn't has this nid data : " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
//        if (esEntity.getStatus() != 0) {
//            logger.warning("work type status is end, so it doesn't to resolve.");
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //逻辑校验 resolve 之前状态必须是 complete
//        if (!esEntity.getWorkType().split("_")[0].equals(RecruitmentConstant.WORK_TYPE_COMPLETE)) {
//            logger.warning("work type status exception, current work type is " + esEntity.getWorkType() + ", but expect work type is " + RecruitmentConstant.WORK_TYPE_COMPLETE + "_" + RecruitmentUtil.getTurns(esEntity.getWorkType()));
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());

        //2.更新Es status work_type
        if (status == 1) {
            RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), RecruitmentConstant.WORK_TYPE_PASS + "_" + turns);
            esEntity.setWorkType(RecruitmentConstant.WORK_TYPE_PASS + "_" + turns);
        } else {
            RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), RecruitmentConstant.WORK_TYPE_NO_PASS + "_" + turns);
            esEntity.setWorkType(RecruitmentConstant.WORK_TYPE_NO_PASS + "_" + turns);
        }
        //3.send flow
        sendFlow(esEntity, status);
        //4.rite logs
        doWriteLogs(esEntity, status, opUser);
        //5.send notice
        doSendNotice(esEntity, status);
        return R.ok();
    }

    //send flow
    private void sendFlow(FlowEsEntity entity, int status) {
        FlowReceiveInput receive = new FlowReceiveInput();
        receive.setNid(entity.getNid());
        receive.setRpc(true);
        receive.setUsername(entity.getUsername());
        receive.setEmail(entity.getEmail());
        receive.setWorkType(entity.getWorkType());
        if (status == 1) {
            receive.setModuleStatus(1);
        } else {
            receive.setModuleStatus(0);
        }
        flowCallBackService.callback(receive);
        logger.info("send flow with info : " + JSON.toJSONString(receive));
    }

    /**
     * 发送日志
     * @param entity entity
     */
    private void doWriteLogs(FlowEsEntity entity, int status, String opUser) {
        TbLogs logs = buildLogsCondition(entity, status, opUser);
        kafkaTemplate.send(RecruitmentConstant.TOPIC_LOGS, JSON.toJSONString(logs));
        logger.info("send logs, topic is " + RecruitmentConstant.TOPIC_LOGS + ", and send log data : " + JSON.toJSONString(logs));
    }


    /**
     *构造log数据
     * @param entity entity
     * @return TbLogs
     */
    private TbLogs buildLogsCondition(FlowEsEntity entity, int status, String opUser){
        TbLogs logs = new TbLogs();
        logs.setNid(entity.getNid());
        logs.setUsername(entity.getUsername());
        logs.setOpUser(opUser);
        logs.setOpTime(RecruitmentUtil.getTime());

        if (status == 1) {
            logs.setOpDetails("面试通过");
        } else {
            logs.setOpDetails("面试未通过");
        }
        logs.setWorkType(entity.getWorkType());
        logs.setOpExt(null);
        return logs;
    }


    private void doSendNotice(FlowEsEntity input, Integer status) {
        TbNotice notice = new TbNotice();
        notice.setNid(input.getNid());
        notice.setWorkType(input.getWorkType());
        notice.setReceiveUser(RecruitmentUtil.getUsername(input.getNid()));
        notice.setReceiveAddress(input.getEmail());
        notice.setSendUser(input.getOpUser());
        notice.setSendTime(RecruitmentUtil.getTime());
        notice.setTitle(EMAIL_INTERVIEW_RESULT_NOTE_TITLE);
        String nickName = StringUtils.isEmpty(input.getRealName()) ? RecruitmentUtil.getUsername(input.getNid()) : input.getRealName();
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(input.getWorkType());
        if (status == 0) {
            //不通过
            notice.setContent(getNoPassContent(nickName, String.valueOf(turns), RecruitmentUtil.getFormatTime()));
        } else if (isEndFlow(turns, input.getServiceLine(), input.getVersion())) {
            //通过 结束
            notice.setContent(getPassEndContent(nickName,RecruitmentUtil.getFormatTime()));
        } else {
            //通过 未结束
            notice.setContent(getPassContent(nickName,String.valueOf(turns), RecruitmentUtil.getFormatTime()));
        }
        kafkaTemplate.send(RecruitmentConstant.NOTICE_RECEIVE_TOPIC, JSON.toJSONString(notice));
        logger.info("send notice with data : " + JSON.toJSONString(notice));
    }

    @Value("${EMAIL_INTERVIEW_RESULT_NOTE_TITLE}")
    private String EMAIL_INTERVIEW_RESULT_NOTE_TITLE;

    @Value("${EMAIL_INTERVIEW_PAST_RESULT_NOTE_CONTENT}")
    private String EMAIL_INTERVIEW_PAST_RESULT_NOTE_CONTENT;

    @Value("${EMAIL_INTERVIEW_END_PAST_RESULT_NOTE_CONTENT}")
    private String EMAIL_INTERVIEW_END_PAST_RESULT_NOTE_CONTENT;

    @Value("${EMAIL_INTERVIEW_NOTPAST_RESULT_NOTE_CONTENT}")
    private String EMAIL_INTERVIEW_NOTPAST_RESULT_NOTE_CONTENT;

    private String getPassContent(String name, String itwType, String time) {
        String content = EMAIL_INTERVIEW_PAST_RESULT_NOTE_CONTENT;
        content = content.replace("{{name}}", name);
        content = content.replace("{{itw_type}}", itwType);
        return content.replace("{{time}}", time);
    }

    private String getPassEndContent(String name, String time) {
        String content = EMAIL_INTERVIEW_END_PAST_RESULT_NOTE_CONTENT;
        content = content.replace("{{name}}", name);
        return content.replace("{{time}}", time);
    }

    private String getNoPassContent(String name, String itwType, String time) {
        String content = EMAIL_INTERVIEW_NOTPAST_RESULT_NOTE_CONTENT;
        content = content.replace("{{name}}", name);
        content = content.replace("{{itw_type}}", itwType);
        return content.replace("{{time}}", time);
    }

    private boolean isEndFlow(int turns, String serviceLine, int version) {

        TbServiceLine query = new TbServiceLine();
        query.setServiceLine(serviceLine);
        query.setVersion(version);
        List<TbServiceLine> lines = serviceLineSearchService.query(query);

        if (lines == null || lines.size() == 0) {
            ExceptionCast.exception("service line info is empty.");
        }

        return turns == lines.get(0).getTurns();
    }
}
