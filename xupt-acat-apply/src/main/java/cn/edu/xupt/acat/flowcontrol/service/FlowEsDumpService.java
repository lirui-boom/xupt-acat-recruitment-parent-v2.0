package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.lib.response.R;

public interface FlowEsDumpService {
    public abstract R dumpToEs(FlowEsEntity input);
}
