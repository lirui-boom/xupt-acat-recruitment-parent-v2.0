package cn.edu.xupt.acat.apply.service;

import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.lib.response.R;

public interface ApplyReceiveService {
    public abstract R receive(FlowSendInput input);
}
