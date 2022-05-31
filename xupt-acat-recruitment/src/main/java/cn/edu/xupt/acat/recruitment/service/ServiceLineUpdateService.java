package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;

public interface ServiceLineUpdateService {
    public abstract R update(TbServiceLine serviceLine);
}
