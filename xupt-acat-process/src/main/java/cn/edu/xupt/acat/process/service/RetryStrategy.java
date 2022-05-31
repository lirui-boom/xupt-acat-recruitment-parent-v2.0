package cn.edu.xupt.acat.process.service;


import cn.edu.xupt.acat.lib.model.FlowReceiveInput;

/**
 * rpc重试策略抽象类
 *
 * 策略模式
 */
public interface RetryStrategy {
    void retry(FlowReceiveInput input);
}
