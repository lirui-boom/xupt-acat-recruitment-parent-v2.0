package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.apply.service.ApplyReceiveService;
import cn.edu.xupt.acat.flowcontrol.dao.FlowDao;
import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.entity.TbFlow;
import cn.edu.xupt.acat.flowcontrol.service.library.FlowControlCodeEnum;
import cn.edu.xupt.acat.flowcontrol.service.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.service.library.FlowControlUtil;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.RunNextFlowService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowModel;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.service.RecruitmentReceiveService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RunNextFlowServiceImpl implements RunNextFlowService {

    private static Logger logger = Logger.getLogger(RunNextFlowServiceImpl.class.toString());

    @Reference
    private RecruitmentReceiveService recruitmentReceiveService;

    @Reference
    private ApplyReceiveService applyReceiveService;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private FlowDao flowDao;

    @Resource
    private FlowEsDumpService flowEsDumpService;

    /**
     * 流转流程
     * @param input input
     * @param serviceLine serviceLine
     */
    @Override
    public void runNextFlow(FlowReceiveInput input, TbServiceLine serviceLine) {

        String next = FlowControlUtil.getNextFlow(input.getWorkType(), JSON.parseArray(serviceLine.getWorkDetails(), FlowModel.class));

        if (next != null && next.equals(FlowControlConstant.NOT_FOUND_NEXT_FLOW)) {
            logger.warning("work detail property error, not find next flow. current is " + input.getWorkType() + ", but work detail is " + JSON.toJSONString(serviceLine.getWorkDetails()));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_WORK_FLOW_STATUS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_WORK_FLOW_STATUS_EXCEPTION.getCode());
        }

        //流程是否结束
        int status = 0;

        //是最后一个流程 并且 通过
        if (next == null && input.getModuleStatus() == 1) {
            status = 1;
            //面试不通过
        } else if (input.getModuleStatus() == 0) {
            status = 2;
        }

        //调用模块 面试通过了 但是不是最后一个流程
        if (next != null && input.getModuleStatus() == 1) {
            rpcNextFlow(input, serviceLine, next);
        } else{
            logger.info("this is last flow, and flow info is " + JSON.toJSONString(input));
        }

        if (status != 0) {
            doUpdateStatus(input, status);
        }
        //写操作日志
        doWriteLogs(input, serviceLine, status);
    }


    /**
     * rpc调用
     * @param input input
     * @param serviceLine serviceLine
     * @param next next
     */
    private void rpcNextFlow(FlowReceiveInput input, TbServiceLine serviceLine, String next) {

        if (next.equals(FlowControlConstant.WORK_TYPE_APPLY)) {
            FlowSendInput sendInput = buildApplyCondition(input, serviceLine, next);
            applyReceiveService.receive(sendInput);
            logger.info("send next flow, next module is " + next + ", send data : " + JSON.toJSONString(sendInput));
        } else {
            FlowSendInput sendInput = buildRecruitCondition(input, serviceLine, next);
            recruitmentReceiveService.receive(sendInput);
            logger.info("send next flow, next flow is " + next + ", send data : " + JSON.toJSONString(sendInput));
        }
    }


    /**
     * 构造apply rpc调用参数
     *
     * @param input       input
     * @param serviceLine serviceLine
     * @return FlowSendInput
     */
    private FlowSendInput buildApplyCondition(FlowReceiveInput input, TbServiceLine serviceLine, String next) {
        input.getApplyInfo().setNid(input.getNid());
        input.getApplyInfo().setUsername(input.getUsername());
        FlowSendInput sendInput = new FlowSendInput();
        sendInput.setApplyInfo(input.getApplyInfo());
        sendInput.setNid(input.getNid());
        sendInput.setWorkType(next);
        sendInput.setEmail(input.getEmail());
        sendInput.setUsername(input.getUsername());
        return sendInput;
    }

    /**
     * 构造recruitment rpc调用参数
     *
     * @param input       input
     * @param serviceLine serviceLine
     * @return FlowSendInput
     */
    private FlowSendInput buildRecruitCondition(FlowReceiveInput input, TbServiceLine serviceLine, String next) {
        FlowSendInput sendInput = new FlowSendInput();
        sendInput.setNid(input.getNid());
        sendInput.setWorkType(next);
        sendInput.setUsername(input.getUsername());
        sendInput.setEmail(input.getEmail());
        return sendInput;
    }

    /**
     * 发送日志
     * @param input input
     * @param serviceLine serviceLine
     * @param status status
     */
    private void doWriteLogs(FlowReceiveInput input, TbServiceLine serviceLine, int status) {
        TbLogs logs = buildLogsCondition(input, serviceLine, status);
        kafkaTemplate.send(FlowControlConstant.TOPIC_LOGS, JSON.toJSONString(logs));
        logger.info("send logs, topic is " + FlowControlConstant.TOPIC_LOGS + ", and send log data : " + JSON.toJSONString(logs));
    }


    /**
     *构造log数据
     * @param input input
     * @param serviceLine serviceLine
     * @return TbLogs
     */
    private TbLogs buildLogsCondition(FlowReceiveInput input, TbServiceLine serviceLine, int status){
        TbLogs logs = new TbLogs();
        logs.setNid(input.getNid());
        logs.setUsername(input.getUsername());
        logs.setOpUser(FlowControlConstant.DEFAULT_OP_USER_FLOW);
        logs.setOpTime(FlowControlUtil.getTime());
        if (input.getWorkType().equals(FlowControlConstant.WORK_TYPE_NO_APPLY)) {
            logs.setOpDetails("送入报名系统");
        } else if (status == 0) {
            logs.setOpDetails("送入面试系统");
        } else {
            logs.setOpDetails("面试流程结束");
        }
        logs.setWorkType(input.getWorkType());
        logs.setOpExt(null);
        return logs;
    }

    private void doUpdateStatus(FlowReceiveInput input, int status) {

        //update db
        QueryWrapper<TbFlow> wrapper = new QueryWrapper<>();
        wrapper.eq("nid", input.getNid());
        List<TbFlow> flows = flowDao.selectList(wrapper);
        if (flows == null) {
            logger.warning("flow info is empty");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        TbFlow flow = flows.get(0);
        flow.setStatus(status);
        flowDao.updateById(flow);

        //update es
        FlowEsEntity esEntity = new FlowEsEntity();
        esEntity.setNid(input.getNid());
        esEntity.setStatus(status);
        flowEsDumpService.dumpToEs(esEntity);

        logger.info("work flow is end, update db and es status is " + status);
    }
}
