package cn.edu.xupt.acat.report.dao;

import cn.edu.xupt.acat.report.domain.entity.TbReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportDao extends BaseMapper<TbReport> {
}
