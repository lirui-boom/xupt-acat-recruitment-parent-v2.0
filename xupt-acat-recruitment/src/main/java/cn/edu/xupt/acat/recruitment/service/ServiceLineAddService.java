package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;

public interface ServiceLineAddService {
    public abstract R addServiceLine(TbServiceLine serviceLine);
}
