package cn.edu.xupt.acat.process.service.impl;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.process.library.ProcessCodeEnum;
import cn.edu.xupt.acat.process.library.ProcessConstant;
import cn.edu.xupt.acat.process.library.ProcessUtil;
import cn.edu.xupt.acat.process.service.RetryStrategy;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service
public class ProducerCallBackListener implements ProducerListener<String, String> {

    private static Logger logger = Logger.getLogger(ProducerCallBackListener.class.toString());

    @Reference
    private FlowReceiveService flowReceiveService;

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @Resource(name = "AIMDRetryStrategy")
    private RetryStrategy retryStrategy;

    @Override
    public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {
        logger.info(ProcessCodeEnum.SEND_QUEUE_SUCCESS.getMsg() + ":" + " topic:" + topic + " partition:" + partition + " value:" + value + " recordMetadata:" + JSON.toJSONString(recordMetadata));
    }

    @Override
    public void onError(String topic, Integer partition, String key, String value, Exception exception) {
        logger.warning(ProcessCodeEnum.SEND_QUEUE_FAIL.getMsg() + ":" + " topic:" + topic + " partition:" + partition + " value:" + value + " exception:" + exception.getMessage());
        retry(value);
    }

    private void retry(String value){
        FlowReceiveInput input = ProcessUtil.buildRpcCondition(JSON.parseObject(value,FlowReceiveInput.class));
        retryStrategy.retry(input);
    }
}