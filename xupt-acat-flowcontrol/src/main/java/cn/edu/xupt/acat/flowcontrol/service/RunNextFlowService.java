package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;

public interface RunNextFlowService {
    public abstract void runNextFlow(FlowReceiveInput input, TbServiceLine serviceLine);
}
