package cn.edu.xupt.acat.report.service;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.domain.vo.ReportSearchVo;

import java.util.List;

public interface ReportSearchService {
    public abstract SearchPage<List<TbReport>> search(ReportSearchVo input);
}
