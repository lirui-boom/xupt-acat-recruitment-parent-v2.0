package cn.edu.xupt.acat.logs.service;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.logs.domain.vo.LogsSearchVo;

import java.util.List;

public interface LogsSearchService {
    public abstract List<TbLogs> query(TbLogs logs);
    public abstract SearchPage<List<TbLogs>> search(LogsSearchVo vo);
}
