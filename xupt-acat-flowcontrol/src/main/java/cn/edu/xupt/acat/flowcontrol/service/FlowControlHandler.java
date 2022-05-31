package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;

public interface FlowControlHandler {
    public abstract void handler(FlowReceiveInput input);
}
