package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;

public interface FlowReceiveService {
    public abstract R receive(FlowReceiveInput input);
}
