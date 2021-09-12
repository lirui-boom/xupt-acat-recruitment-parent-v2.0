package cn.edu.xupt.acat.report.domain.vo;

import cn.edu.xupt.acat.report.domain.entity.TbReport;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportSearchVo implements Serializable {
    private TbReport report;
    private int pageNum;
    private int pageSize;
}
