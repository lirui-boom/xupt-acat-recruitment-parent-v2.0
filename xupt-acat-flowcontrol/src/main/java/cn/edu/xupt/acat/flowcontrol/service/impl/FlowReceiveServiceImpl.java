package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.dao.FlowDao;
import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.entity.TbFlow;
import cn.edu.xupt.acat.flowcontrol.library.EsSearch;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlCodeEnum;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlUtil;
import cn.edu.xupt.acat.flowcontrol.service.*;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.util.Util;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
@Component
public class FlowReceiveServiceImpl implements FlowReceiveService {

    private static Logger logger = Logger.getLogger(FlowReceiveServiceImpl.class.toString());

    @Autowired
    private FlowControlService flowControlService;

    @Autowired
    private FlowControlHandler flowControlHandler;

    @Resource
    private FlowDao flowDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private EsSearch esSearch;

    @Autowired
    private RunNextFlowService runNextFlowService;

    @Autowired
    private FlowSearchService flowSearchService;

    /**
     * 参数化校验
     * @param input input
     */
    private void getParam(FlowReceiveInput input) {
        if (input == null) {
            logger.warning("input is empty. ");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getUsername()) || StringUtils.isEmpty(input.getServiceLine()) || StringUtils.isEmpty(input.getVersion())) {
            logger.warning("can not generate nid " + ":" + JSON.toJSONString(input));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), FlowControlCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(input.getWorkType())) {
            logger.warning("work type is empty, so set work type first flow. ");
            input.setWorkType(FlowControlConstant.WORK_TYPE_NO_APPLY);
        }

        //记录数据来源
        if (input.isRpc()) {
            logger.info("request from rpc: " + JSON.toJSONString(input));
        }

        if (!input.isRpc()) {
            logger.info("request from pipe: " + JSON.toJSONString(input));
        }
    }

    @Override
    public R receive(FlowReceiveInput input) {
        getParam(input);
        execute(input);
        return R.ok();
    }

    @KafkaListener(topics = {"FLOW_CONTROL_RECEIVE"})
    public void receive(ConsumerRecord<String, String> record) {
        FlowReceiveInput input = JSON.parseObject(record.value(), FlowReceiveInput.class);
        receive(input);
    }

    /**
     * 执行
     * @param input input
     */
    private void execute(FlowReceiveInput input){

        String nid = FlowControlUtil.getNid(input.getUsername(), input.getServiceLine(), input.getVersion());
        input.setNid(nid);

        //service_line info
        TbServiceLine serviceLine = new TbServiceLine();
        serviceLine.setServiceLine("java");
        serviceLine.setQps(3);
//        TbServiceLine serviceLine = esSearch.remoteServiceLine(input);
//        if (serviceLine == null) {
//            logger.warning("serviceLine info is empty.");
//            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getMsg(),FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getCode());
//        }

        //1.流量限制
        if (!flowControlService.isPass(nid, serviceLine)) {
            logger.warning("qps overflow, execute flow control handler.");
            flowControlHandler.handler(input);
            return;
        }

        //2.合法性校验
        if (checkExist(nid)) {
            logger.warning("nid is exist.");
            ExceptionCast.exception(FlowControlCodeEnum.DATA_EXISTED_EXCEPTION.getMsg(),FlowControlCodeEnum.DATA_EXISTED_EXCEPTION.getCode());
        }

        //3.获取user详细信息
        if (serviceLine.getStatus() == 0) {
            logger.warning("serviceLine status is closed(0).");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_SERVICE_LINE_STATUS_EXCEPTION.getMsg(),FlowControlCodeEnum.ILLEGAL_SERVICE_LINE_STATUS_EXCEPTION.getCode());
        }

        TbUser user = esSearch.remoteUser(input);
        if (user == null) {
            logger.warning("user info is empty.");
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getMsg(),FlowControlCodeEnum.ILLEGAL_REQUEST_EXCEPTION.getCode());
        }
        input.setEmail(user.getEmail());
        //4.insert es & db
        doSaveInfo(input, serviceLine, user);
        //5.runNextFlow
        runNextFlowService.runNextFlow(input, serviceLine);
    }

    /**
     * 数据存储
     * @param input input
     * @param serviceLine serviceLine
     * @param user user
     */
    private TbFlow doSaveInfo(FlowReceiveInput input, TbServiceLine serviceLine, TbUser user) {
        TbFlow flow = buildDbCondition(input);
        flowDao.insert(flow);
        IndexQuery condition = buildEsCondition(flow, serviceLine, user, input);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(FlowEsEntity.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
        logger.info("insert db and es with data : " + JSON.toJSONString(flow));
        return flow;
    }

    /**
     * 构造DB数据
     * @param input input
     * @return TbFlow
     */
    private TbFlow buildDbCondition(FlowReceiveInput input) {
        TbFlow flow = new TbFlow();
        flow.setNid(input.getNid());
        flow.setUsername(input.getUsername());
        flow.setServiceLine(input.getServiceLine());
        flow.setVersion(input.getVersion());
        flow.setWorkType(FlowControlConstant.WORK_TYPE_NO_APPLY);
        Date now = Util.getTime();
        flow.setCreateTime(now);
        flow.setUpdateTime(now);
        flow.setStatus(0);
        return flow;
    }

    /**
     * 构造ES数据
     * @param flow flow
     * @param serviceLine serviceLine
     * @param user user
     * @return IndexQuery
     */
    private IndexQuery buildEsCondition(TbFlow flow, TbServiceLine serviceLine, TbUser user, FlowReceiveInput input) {
        FlowEsEntity esEntity = new FlowEsEntity();
        esEntity.setNid(flow.getNid());
        esEntity.setUsername(flow.getUsername());
        esEntity.setEmail(user.getEmail());
        esEntity.setOpUser(FlowControlConstant.DEFAULT_OP_USER_FLOW);
        esEntity.setServiceLine(flow.getServiceLine());
        esEntity.setVersion(flow.getVersion());
        esEntity.setTurns(serviceLine.getTurns());
        esEntity.setStatus(0);
        esEntity.setWorkType(FlowControlConstant.WORK_TYPE_NO_APPLY);
        esEntity.setNickName(user.getNickName());
        if (input.getApplyInfo() != null) {
            esEntity.setRealName(input.getApplyInfo().getRealName());
            esEntity.setClassName(input.getApplyInfo().getClassName());
            esEntity.setPhone(input.getApplyInfo().getPhone());
            esEntity.setSnumber(input.getApplyInfo().getSnumber());
            esEntity.setSex(input.getApplyInfo().getSex());
        }
        return  new IndexQueryBuilder()
                .withId(Long.toString(flow.getId()))
                .withObject(esEntity)
                .build();
    }


    /**
     * 检查数据是否存在
     * @param nid nid
     * @return boolean
     */
    private boolean checkExist(String nid) {
        FlowEsEntity entity = new FlowEsEntity();
        entity.setNid(nid);
        List<FlowEsEntity> query = flowSearchService.query(entity);
        return query != null && query.size() > 0;
    }
}
