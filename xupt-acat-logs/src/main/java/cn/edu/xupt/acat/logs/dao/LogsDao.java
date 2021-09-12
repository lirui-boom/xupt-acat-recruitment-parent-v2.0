package cn.edu.xupt.acat.logs.dao;

import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogsDao extends BaseMapper<TbLogs> {
}
