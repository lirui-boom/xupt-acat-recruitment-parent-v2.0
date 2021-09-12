package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.logs.domain.TbLogs;
import cn.edu.xupt.acat.recruitment.dao.EvaluateDao;
import cn.edu.xupt.acat.recruitment.domain.entity.TbEvaluate;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueNodeVo;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.library.RedisLock;
import cn.edu.xupt.acat.recruitment.service.RecruitmentSubmitService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RecruitmentSubmitServiceImpl implements RecruitmentSubmitService {

    private static Logger logger = Logger.getLogger(RecruitmentSubmitServiceImpl.class.toString());

    @Resource
    private EvaluateDao evaluateDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisLock lock;

    @Reference
    private FlowEsDumpService flowEsDumpService;

    @Reference
    private FlowSearchService flowSearchService;

    @Override
    @Transactional
    public R submit(TbEvaluate evaluate) {
        getParam(evaluate);
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, evaluate.getNid());
        if (esEntity == null) {
            logger.warning("flow es doesn't has this nid data : " + evaluate.getNid());
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
//        if (esEntity.getStatus() != 0) {
//            logger.warning("work type status is end, so it doesn't to submit.");
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //逻辑校验 submit之前状态必须是doing
//        if (!esEntity.getWorkType().split("_")[0].equals(RecruitmentConstant.WORK_TYPE_DOING)) {
//            logger.warning("work type status exception, current work type is " + esEntity.getWorkType() + ", but expect work type is " + RecruitmentConstant.WORK_TYPE_DOING + "_" + RecruitmentUtil.getTurns(esEntity.getWorkType()));
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        evaluate.setWorkType(esEntity.getWorkType());
        doSaveInfo(evaluate);
        //从队列删除
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());
        doRemoveQueue(evaluate.getNid(),turns);
        //release lock
        doReleaseLock(evaluate);
        //改es workType
        RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, evaluate.getNid(), evaluate.getWorkType());
        //writ eLogs
        //写操作日志
        doWriteLogs(evaluate);
        return R.ok().put("data", evaluate);
    }

    /**
     * 参数校验
     * @param evaluate evaluate
     */
    private void getParam(TbEvaluate evaluate) {

        if (evaluate == null) {
            logger.warning("evaluate info is empty. ");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(evaluate.getNid())) {
            logger.warning("nid info is empty. ");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (StringUtils.isEmpty(evaluate.getEvaluateUser())) {
            logger.warning("evaluate user info is empty. ");
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        logger.info("receive evaluate info is : " + JSON.toJSONString(evaluate));
    }

    private void doSaveInfo(TbEvaluate evaluate) {
        Date now = RecruitmentUtil.getTime();
        evaluate.setCreateTime(now);
        evaluate.setUpdateTime(now);
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(evaluate.getWorkType());
        evaluate.setWorkType(RecruitmentConstant.WORK_TYPE_COMPLETE + "_" + turns);
        evaluateDao.insert(evaluate);
        IndexQuery condition = buildEsCondition(evaluate);
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(TbEvaluate.class);
        elasticsearchRestTemplate.index(condition,indexCoordinates);
        logger.info("insert into db and es with data : " + JSON.toJSONString(evaluate));
    }

    /**
     * 构造ES插入数据
     * @param evaluate evaluate
     * @return IndexQuery
     */
    private IndexQuery buildEsCondition(TbEvaluate evaluate){
        return  new IndexQueryBuilder()
                .withId(Long.toString(evaluate.getId()))
                .withObject(evaluate)
                .build();
    }

    private void doReleaseLock(TbEvaluate evaluate) {
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(evaluate.getWorkType());
        String lockKey = RecruitmentUtil.REDIS_LOCK_PREFIX + evaluate.getNid() + "_" + turns;
        try {
            lock.unlock(lockKey, evaluate.getEvaluateUser());
        } catch (Exception e) {
        }
    }

    /**
     * 发送日志
     * @param evaluate
     */
    private void doWriteLogs(TbEvaluate evaluate) {
        TbLogs logs = buildLogsCondition(evaluate);
        kafkaTemplate.send(RecruitmentConstant.TOPIC_LOGS, JSON.toJSONString(logs));
        logger.info("send logs, topic is " + RecruitmentConstant.TOPIC_LOGS + ", and send log data : " + JSON.toJSONString(logs));
    }


    /**
     *构造log数据
     * @param evaluate evaluate
     * @return TbLogs
     */
    private TbLogs buildLogsCondition(TbEvaluate evaluate){
        TbLogs logs = new TbLogs();
        logs.setNid(evaluate.getNid());
        logs.setUsername(RecruitmentUtil.getUsername(evaluate.getNid()));
        logs.setOpUser(evaluate.getEvaluateUser());
        logs.setOpTime(RecruitmentUtil.getTime());
        logs.setOpDetails("提交评价");
        logs.setWorkType(evaluate.getWorkType());
        logs.setOpExt(null);
        return logs;
    }

    private void doRemoveQueue(String nid, int turns) {
        String queueName = RecruitmentUtil.getRedisQueueName(nid, turns);
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo vo : list) {
            if (vo.getNid().equals(nid)) {
                queue.remove(1, vo);
                return;
            }
        }
    }
}
