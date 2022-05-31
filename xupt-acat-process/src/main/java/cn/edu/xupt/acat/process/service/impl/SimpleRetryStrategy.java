package cn.edu.xupt.acat.process.service.impl;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.process.service.RetryStrategy;
import cn.edu.xupt.acat.process.task.SimpleRetryTask;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * 非阻塞的简单重试算法
 */
@Component
public class SimpleRetryStrategy implements RetryStrategy {

    private static Logger logger = Logger.getLogger(SimpleRetryStrategy.class.toString());

    @Reference
    private FlowReceiveService flowReceiveService;

    @Override
    public void retry(FlowReceiveInput input) {
        Thread t = new Thread(new SimpleRetryTask(flowReceiveService, input));
        t.start();
        logger.info("start thread : " + t.getName() + " for retry with rpc method.");
    }
}
