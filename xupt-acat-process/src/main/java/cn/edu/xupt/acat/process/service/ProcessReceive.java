package cn.edu.xupt.acat.process.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.process.domain.vo.ProcessInput;

public interface ProcessReceive {
    public abstract R receive(ProcessInput input);
}
