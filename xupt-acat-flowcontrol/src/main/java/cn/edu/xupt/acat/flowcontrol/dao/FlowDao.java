package cn.edu.xupt.acat.flowcontrol.dao;

import cn.edu.xupt.acat.flowcontrol.domain.entity.TbFlow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowDao extends BaseMapper<TbFlow> {

}
