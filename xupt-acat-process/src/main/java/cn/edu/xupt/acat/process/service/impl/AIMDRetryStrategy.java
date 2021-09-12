package cn.edu.xupt.acat.process.service.impl;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.process.service.RetryStrategy;
import cn.edu.xupt.acat.process.task.AIMDRetryTask;
import cn.edu.xupt.acat.process.task.SimpleRetryTask;
import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * 加性增、乘性减（AIMD算法）
 */
@Slf4j
@Component
public class AIMDRetryStrategy implements RetryStrategy {

    private static Logger logger = Logger.getLogger(SimpleRetryStrategy.class.toString());
    /**
     * 拥塞阈值初始值（ms毫秒）
     */
    private static int baseThresh = 150;

    private static int inc = 25;

    @Reference
    private FlowReceiveService flowReceiveService;

    @Override
    public void retry(FlowReceiveInput input) {
        Thread t = new Thread(new AIMDRetryTask(flowReceiveService, input, baseThresh, inc));
        t.start();
        logger.info("start thread : " + t.getName() + " for retry with rpc method.");
    }
}
