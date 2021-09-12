package cn.edu.xupt.acat.recruitment.service;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.domain.vo.ServiceLineSearchVo;

import java.util.List;

public interface ServiceLineSearchService {
    public abstract List<TbServiceLine> query(TbServiceLine serviceLine);
    public abstract SearchPage<List<TbServiceLine>> search(ServiceLineSearchVo vo);
}
