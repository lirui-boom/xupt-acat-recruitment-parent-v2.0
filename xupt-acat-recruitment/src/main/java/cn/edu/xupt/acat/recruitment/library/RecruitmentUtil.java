package cn.edu.xupt.acat.recruitment.library;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.util.Util;
import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.logging.Logger;

public class RecruitmentUtil extends Util {

    private static Logger logger = Logger.getLogger(RecruitmentUtil.class.toString());
    public static String REDIS_QUEUE_PREFIX = "recruitment_queue_";
    public static String REDIS_LOCK_PREFIX = "queue_lock_";

    /**
     * 获取当前redis队列名称
     * @param nid   nid
     * @param turns turns
     * @return String
     */
    public static String getRedisQueueName(String nid, int turns) {
        String serviceLine = getServiceLine(nid);
        String version = getVersion(nid);
        return REDIS_QUEUE_PREFIX + serviceLine + "@" + version + "_" + turns;
    }

    /**
     * 修改flow es workType
     * @param service
     * @param nid
     * @param workType
     */
    public static void dumpWorkTypeToFLowEs(FlowEsDumpService service, String nid, String workType) {
        FlowEsEntity esEntity = new FlowEsEntity();
        esEntity.setNid(nid);
        esEntity.setWorkType(workType);
        service.dumpToEs(esEntity);
        logger.info("send flow dump to es with data : " + JSON.toJSONString(esEntity));
    }

    public static FlowEsEntity getFlowEsEntity(FlowSearchService service, String nid) {
        FlowEsEntity searchEntity = new FlowEsEntity();
        searchEntity.setNid(nid);
        List<FlowEsEntity> query = service.query(searchEntity);

        if (query == null || query.size() == 0) {
            return null;
        }
        logger.info("search flow es info is " + JSON.toJSONString(query.get(0)));
        return query.get(0);
    }
}
