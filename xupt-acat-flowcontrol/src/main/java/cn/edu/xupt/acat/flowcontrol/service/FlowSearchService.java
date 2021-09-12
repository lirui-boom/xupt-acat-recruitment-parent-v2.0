package cn.edu.xupt.acat.flowcontrol.service;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.vo.FlowEsEntityVo;
import cn.edu.xupt.acat.lib.response.SearchPage;

import java.util.List;

public interface FlowSearchService {
    public abstract List<FlowEsEntity> query(FlowEsEntity entity);
    public abstract SearchPage<List<FlowEsEntity>> search(FlowEsEntityVo vo);
}
