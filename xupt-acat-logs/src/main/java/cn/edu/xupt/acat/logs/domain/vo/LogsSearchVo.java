package cn.edu.xupt.acat.logs.domain.vo;

import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogsSearchVo implements Serializable {
    private TbLogs logs;
    private int pageNum;
    private int pageSize;
}

