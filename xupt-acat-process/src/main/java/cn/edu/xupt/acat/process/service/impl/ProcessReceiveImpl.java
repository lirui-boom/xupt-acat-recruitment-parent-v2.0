package cn.edu.xupt.acat.process.service.impl;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.process.domain.vo.ProcessInput;
import cn.edu.xupt.acat.process.library.ProcessConstant;
import cn.edu.xupt.acat.process.library.ProcessUtil;
import cn.edu.xupt.acat.process.service.ProcessReceive;
import com.alibaba.fastjson.JSON;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProcessReceiveImpl implements ProcessReceive {

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @Resource
    private ProducerCallBackListener listener;

    @Override
    public R receive(ProcessInput input) {
        //使用kafka模板发送信息
        FlowReceiveInput message = ProcessUtil.buildMqCondition(input);
        //设置监听
        kafkaTemplate.setProducerListener(listener);
        //发送消息，获取future对象
        kafkaTemplate.send(ProcessConstant.FLOW_RECEIVE_TOPIC, JSON.toJSONString(message));
        return R.ok();
    }
}
