package cn.edu.xupt.acat.logs.service;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;

public interface LogsService {
    public abstract R add(TbLogs logs);
}
