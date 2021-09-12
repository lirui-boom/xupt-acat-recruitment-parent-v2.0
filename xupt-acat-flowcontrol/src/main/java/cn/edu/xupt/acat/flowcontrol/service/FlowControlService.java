package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;

public interface FlowControlService {
    public abstract boolean isPass(String nid, TbServiceLine serviceLine);
}
