package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.library.FlowControlUtil;
import cn.edu.xupt.acat.flowcontrol.library.RedisFlowControl;
import cn.edu.xupt.acat.flowcontrol.service.FlowControlService;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.logging.Logger;


@Service
public class FlowControlServiceImpl implements FlowControlService {

    private static Logger logger = Logger.getLogger(FlowControlServiceImpl.class.toString());

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisFlowControl redisFlowControl;

    @Value("${flow.control.module}")
    private int FLOW_MODULE_QPS;

    @Override
    public boolean isPass(String nid, TbServiceLine serviceLine) {
        //对模块和serviceLine限流
//        return checkModule(FlowControlConstant.FLOW_MODULE_NAME) && checkServiceLine(serviceLine);
        return false;
    }

    /**
     * 模块限流
     * @param module module
     * @return boolean
     */
    private boolean checkModule(String module){
        boolean res =  redisFlowControl.isPass(module, FLOW_MODULE_QPS);
        if (!res) {
            logger.warning("module flow control, qps overflow properties value : " + FLOW_MODULE_QPS);
        }
        return res;
    }

    /**
     * serviceLine限流
     * @param serviceLine serviceLine
     * @return boolean
     */
    private boolean checkServiceLine(TbServiceLine serviceLine){
        boolean res =  redisFlowControl.isPass(serviceLine.getServiceLine(), serviceLine.getQps());
        if (!res) {
            logger.warning("service line flow control, qps overflow properties value : " + serviceLine.getQps());
        }
        return res;
    }
}
