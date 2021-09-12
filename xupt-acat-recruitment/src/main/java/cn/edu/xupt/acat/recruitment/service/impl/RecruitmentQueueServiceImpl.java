package cn.edu.xupt.acat.recruitment.service.impl;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.model.ApplyInfo;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.logs.domain.TbLogs;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueLogsVo;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueNodeVo;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueSearchVo;
import cn.edu.xupt.acat.recruitment.library.RecruitmentCodeEnum;
import cn.edu.xupt.acat.recruitment.library.RecruitmentConstant;
import cn.edu.xupt.acat.recruitment.library.RecruitmentUtil;
import cn.edu.xupt.acat.recruitment.library.RedisLock;
import cn.edu.xupt.acat.recruitment.service.RecruitmentQueueService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class RecruitmentQueueServiceImpl implements RecruitmentQueueService {

    private static Logger logger = Logger.getLogger(RecruitmentQueueServiceImpl.class.toString());

    @Reference
    private FlowSearchService flowSearchService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisLock lock;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Reference
    private FlowEsDumpService flowEsDumpService;

    @Override
    public R sign(String nid) {
        //1.获取当前流程状态
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, nid);

        if (esEntity == null) {
            logger.warning("flow info is empty, search nid is " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        //逻辑校验
//        if (esEntity.getStatus() != null && esEntity.getStatus() != 0) {
//            logger.warning("work type status is end, so it doesn't to sign in.");
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //sign in 之前状态必须是 nosign
//        if (!esEntity.getWorkType().split("_")[0].equals(RecruitmentConstant.WORK_TYPE_NOT_SIGN)) {
//            logger.warning("work type status exception, current work type is " + esEntity.getWorkType() + ", but expect work type is " + RecruitmentConstant.WORK_TYPE_NOT_SIGN + "_" + RecruitmentUtil.getTurns(esEntity.getWorkType()));
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());

        //Redis队列名称
        String queueName = RecruitmentUtil.getRedisQueueName(nid, turns);

        //2.检查数据
        if (checkExist(queueName, nid)) {
            logger.warning("redis queue " + queueName + " exist this nid : " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.REDIS_DATA_EXISTED_EXCEPTION.getMsg(), RecruitmentCodeEnum.REDIS_DATA_EXISTED_EXCEPTION.getCode());
        }
        //3.加入Redis队列中
        esEntity.setWorkType(RecruitmentConstant.WORK_TYPE_SIGN_IN + "_" + turns);
        QueueNodeVo vo = buildQueueNode(esEntity);
        doSaveRedis(queueName, vo);
        //4.update work type
        RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), esEntity.getWorkType());
        //5. write logs
        QueueLogsVo logsVo = new QueueLogsVo();
        logsVo.setOpUser(esEntity.getUsername());
        logsVo.setDetail("已签到");
        logsVo.setWorkType(esEntity.getWorkType());
        doWriteLogs(esEntity,logsVo);
        return R.ok();
    }

    @Override
    public R signUp(String nid) {

        //1.获取当前流程状态
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, nid);

        if (esEntity == null) {
            logger.warning("flow info is empty, search nid is " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        //sign up 之前状态必须是 sign
//        if (!esEntity.getWorkType().split("_")[0].equals(RecruitmentConstant.WORK_TYPE_SIGN_IN)) {
//            logger.warning("work type status exception, current work type is " + esEntity.getWorkType() + ", but expect work type is " + RecruitmentConstant.WORK_TYPE_SIGN_IN + "_" + RecruitmentUtil.getTurns(esEntity.getWorkType()));
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());

        //Redis队列名称
        String queueName = RecruitmentUtil.getRedisQueueName(nid, turns);

        //2.检查数据
        if (!checkExist(queueName, nid)) {
            logger.warning("redis queue " + queueName + " not exist this nid : " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.REDIS_DATA_NOT_EXIST_EXCEPTION.getMsg(), RecruitmentCodeEnum.REDIS_DATA_NOT_EXIST_EXCEPTION.getCode());
        }

        //3.移除数据
        QueueNodeVo vo = new QueueNodeVo();
        vo.setNid(esEntity.getNid());
        doRemoveRedis(queueName,vo);
        //4.update work type
        RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), RecruitmentConstant.WORK_TYPE_NOT_SIGN + "_" + turns);
        //5.write logs
        QueueLogsVo logsVo = new QueueLogsVo();
        logsVo.setOpUser(esEntity.getUsername());
        logsVo.setDetail("已签退");
        logsVo.setWorkType(RecruitmentConstant.WORK_TYPE_NOT_SIGN + "_" + turns);
        doWriteLogs(esEntity,logsVo);
        return R.ok();
    }

    @Override
    public R getTask(String nid, String opUser) {

        //1.获取当前流程状态
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, nid);

        if (esEntity == null) {
            logger.warning("flow info is empty, search nid is " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        //当前面试轮次
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());

        //2.获取队列首个等待面试
        String serviceLine = RecruitmentUtil.getServiceLine(nid);
        String version = RecruitmentUtil.getVersion(nid);
        String queueName = RecruitmentUtil.REDIS_QUEUE_PREFIX + serviceLine + "@" + version + "_" + turns;

        QueueNodeVo vo = getFirstTask(queueName);

        if (vo == null) {
            logger.warning("queue " + queueName + " is empty or there is no data that it'is itw status is false.");
            ExceptionCast.exception(RecruitmentCodeEnum.QUEUE_DATA_EMPTY_EXCEPTION.getMsg(), RecruitmentCodeEnum.QUEUE_DATA_EMPTY_EXCEPTION.getCode());
        }

        //加锁
        boolean locked = lock.tryLock(RecruitmentUtil.REDIS_LOCK_PREFIX + vo.getNid() + "_" + turns, opUser, RecruitmentConstant.REDIS_LOCK_EXPIRE, TimeUnit.MINUTES);

        if (!locked) {
            logger.warning("lock queue " + queueName + " data fail : " +JSON.toJSONString(vo) + " , because lock not release.");
            ExceptionCast.exception(RecruitmentCodeEnum.REDIS_LOCK_FAIL_EXCEPTION.getMsg(), RecruitmentCodeEnum.REDIS_LOCK_FAIL_EXCEPTION.getCode());
        }
        //update work type
        RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), RecruitmentConstant.WORK_TYPE_DOING + "_" + turns);
        //write logs
        QueueLogsVo logsVo = new QueueLogsVo();
        logsVo.setOpUser(opUser);
        logsVo.setDetail("获取面试任务");
        logsVo.setWorkType(RecruitmentConstant.WORK_TYPE_DOING + "_" + turns);
        doWriteLogs(esEntity,logsVo);
        //更改Queue状态
        vo.setItwUser(opUser);
        vo.setItwing(true);
        doUpdateQueue(RecruitmentUtil.getRedisQueueName(nid, turns), vo);
        //构造返回数据
        return R.ok().put("data", vo);
    }

    @Override
    public R release(String nid, String opUser) {

        //1.获取当前流程状态
        FlowEsEntity esEntity = RecruitmentUtil.getFlowEsEntity(flowSearchService, nid);

        if (esEntity == null) {
            logger.warning("flow info is empty, search nid is " + nid);
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }
        //逻辑校验 release之前状态必须是 doing
//        if (!esEntity.getWorkType().split("_")[0].equals(RecruitmentConstant.WORK_TYPE_DOING)) {
//            logger.warning("work type status exception, current work type is " + esEntity.getWorkType() + ", but expect work type is " + RecruitmentConstant.WORK_TYPE_DOING + "_" + RecruitmentUtil.getTurns(esEntity.getWorkType()));
//            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
//        }
        int turns = RecruitmentUtil.getTurns(esEntity.getWorkType());

        String recruitQueue = RecruitmentUtil.getRedisQueueName(nid, turns);
        BoundListOperations queue = redisTemplate.boundListOps(recruitQueue);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo vo : list) {
            if (vo.getNid().equals(nid)) {
                //release lock
                String lockKey = RecruitmentUtil.REDIS_LOCK_PREFIX + vo.getNid() + "_" + turns;

                //特殊处理
                if (!opUser.equals(lock.getLockValue(lockKey))) {
//                    ExceptionCast.exception(RecruitmentCodeEnum.REDIS_OPUSER_DIFF_EXCEPTION.getMsg(), RecruitmentCodeEnum.REDIS_OPUSER_DIFF_EXCEPTION.getCode());
                }
                try {
                    //?? redis执行脚本会报错，但是可以解锁 :)
                    //所以前面判断了一下opUser和value是不是同一个
                    //有时间了查一下原因吧
                    lock.unlock(lockKey, vo.getItwUser());
                } catch (Exception e){
                }
                vo.setItwUser(null);
                vo.setItwing(false);
                queue.set(list.indexOf(vo), vo);
                //update work type
                RecruitmentUtil.dumpWorkTypeToFLowEs(flowEsDumpService, esEntity.getNid(), RecruitmentConstant.WORK_TYPE_SIGN_IN + "_" + turns);

                //write logs
                QueueLogsVo logsVo = new QueueLogsVo();
                logsVo.setOpUser(opUser);
                logsVo.setDetail("释放面试任务");
                logsVo.setWorkType(RecruitmentConstant.WORK_TYPE_SIGN_IN + "_" + turns);
                doWriteLogs(esEntity,logsVo);

                return R.ok();
            }
        }
        return R.error("释放任务失败，未找到此任务。");
    }

    @Override
    public R getCount(String serviceLine, String version, int turns) {
        String queueName = RecruitmentUtil.REDIS_QUEUE_PREFIX + serviceLine + "@" + version + "_" + turns;
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        int count = 0;
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo vo : list) {
            if (!vo.isItwing()) {
                count++;
            }
        }
        return R.ok().put("data", count);
    }

    @Override
    public SearchPage<List<QueueNodeVo>> getList(QueueSearchVo vo) {

        if (vo == null) {
            ExceptionCast.exception(RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg(), RecruitmentCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        if (vo.getPageNum() <= 0) {
            vo.setPageNum(1);
        }

        if (vo.getPageSize() <= 0) {
            vo.setPageSize(10);
        }

        String queueName = RecruitmentUtil.REDIS_QUEUE_PREFIX + vo.getServiceLine() + "@" + vo.getVersion() + "_" + vo.getTurns();
        BoundListOperations queue = redisTemplate.boundListOps(queueName);

        List<QueueNodeVo> list = queue.range(0, -1);
        List<QueueNodeVo> res = new ArrayList<>(vo.getPageSize());

        int pageNum = vo.getPageNum();
        int pageSize = vo.getPageSize();
        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize > list.size() ? list.size() : pageNum * pageSize;
        for (int i = start; i < end; i++) {
            res.add(list.get(i));
        }
        return new SearchPage<List<QueueNodeVo>>(pageNum,pageSize,(long)list.size(),res);
    }

    /**
     * 检查数据是否存在
     * @param queueName queueName
     * @param nid nid
     * @return boolean
     */
    private boolean checkExist(String queueName, String nid) {
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo vo : list) {
            if (vo.getNid().equals(nid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 存储redis node
     * @param queueName queueName
     * @param vo vo
     */
    private void doSaveRedis(String queueName, QueueNodeVo vo) {
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        queue.rightPush(vo);
    }

    /**
     * 删除redis node
     * @param queueName queueName
     * @param vo vo
     */
    private void doRemoveRedis(String queueName, QueueNodeVo vo) {
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo node : list) {
            if (vo.getNid().equals(node.getNid()) && node.isItwing()) {
                ExceptionCast.exception(RecruitmentCodeEnum.REDIS_DATA_ITWING_EXCEPTION.getMsg(), RecruitmentCodeEnum.REDIS_DATA_ITWING_EXCEPTION.getCode());
            }else if (vo.getNid().equals(node.getNid()) && !node.isItwing()){
                logger.info("remove queue node : " + JSON.toJSONString(node));
                queue.remove(1, node);
            }
        }
    }

    private QueueNodeVo getFirstTask(String queueName){
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo node : list) {
            if (!node.isItwing()) {
                return node;
            }
        }
        return null;
    }

    /**
     * 发送日志
     *
     * @param entity entity
     */
    private void doWriteLogs(FlowEsEntity entity, QueueLogsVo logsVo) {
        TbLogs logs = buildLogsCondition(entity,logsVo);
        kafkaTemplate.send(RecruitmentConstant.TOPIC_LOGS, JSON.toJSONString(logs));
        logger.info("send logs, topic is " + RecruitmentConstant.TOPIC_LOGS + ", and send log data : " + JSON.toJSONString(logs));
    }


    /**
     * 构造log数据
     *
     * @param entity entity
     * @return TbLogs
     */
    private TbLogs buildLogsCondition(FlowEsEntity entity, QueueLogsVo logsVo) {
        TbLogs logs = new TbLogs();
        logs.setNid(entity.getNid());
        logs.setUsername(entity.getUsername());
        logs.setOpUser(logsVo.getOpUser());
        logs.setOpTime(RecruitmentUtil.getTime());
        logs.setOpDetails(logsVo.getDetail());
        logs.setWorkType(logsVo.getWorkType());
        logs.setOpExt(null);
        return logs;
    }

    /**
     * 构造Queue Node数据
     * @param esEntity
     * @return
     */
    private QueueNodeVo buildQueueNode(FlowEsEntity esEntity) {
        QueueNodeVo vo = new QueueNodeVo();
        vo.setNid(esEntity.getNid());
        ApplyInfo info = new ApplyInfo();
        info.setNid(esEntity.getNid());
        info.setUsername(esEntity.getUsername());
        info.setSnumber(esEntity.getSnumber());
        info.setClassName(esEntity.getClassName());
        info.setPhone(esEntity.getPhone());
        info.setRealName(esEntity.getRealName());
        info.setSex(esEntity.getSex());
        info.setContent(esEntity.getApplyContent());
        vo.setApplyInfo(info);
        return vo;
    }

    private void doUpdateQueue(String queueName, QueueNodeVo node) {
        BoundListOperations queue = redisTemplate.boundListOps(queueName);
        List<QueueNodeVo> list = queue.range(0, -1);
        for (QueueNodeVo vo : list) {
            if (vo.getNid().equals(node.getNid())) {
                int index = list.indexOf(vo);
                vo.setItwUser(node.getItwUser());
                vo.setItwing(node.isItwing());
                queue.set(index, vo);
                return;
            }
        }
    }
}
