package cn.edu.xupt.acat.flowcontrol.service.impl;

import cn.edu.xupt.acat.flowcontrol.library.FlowControlConstant;
import cn.edu.xupt.acat.flowcontrol.service.FlowControlHandler;
import cn.edu.xupt.acat.lib.exception.ExceptionCast;
import cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import com.alibaba.fastjson.JSON;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Service
public class FlowControlHandlerImpl implements FlowControlHandler {

    private static Logger logger = Logger.getLogger(FlowControlHandlerImpl.class.toString());

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 来源：pipe重放,重放失败后重试，超过一定次数(3)则丢弃，
     *      rpc抛异常，上游重试
     * @param input
     */
    @Override
    public void handler(FlowReceiveInput input) {
        //上游重试
        if (input.isRpc()) {
            logger.warning("qps overflow, retry. input = " + JSON.toJSONString(input));
            ExceptionCast.exception("qps overflow, retry.", ExceptionCodeEnum.FLOW_CONTROL_LIMIT_EXCEPTION.getCode());
        }
        //丢弃
        if (input.getTtl() == 0) {
            logger.warning("give up message due to ttl is 0, message is " + JSON.toJSONString(input));
            return;
        }
        //重放pipe
        input.setTtl(input.getTtl() - 1);
        kafkaTemplate.send(FlowControlConstant.FLOW_RECEIVE_TOPIC, JSON.toJSONString(input));
    }
}
