package cn.edu.xupt.acat.notices.dao;

import cn.edu.xupt.acat.notices.domain.entity.TbNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeDao extends BaseMapper<TbNotice> {
}
