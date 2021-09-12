package cn.edu.xupt.acat.report.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.report.domain.entity.TbReport;

public interface ReportSubmitService {

    public abstract R submit(TbReport report);
}
