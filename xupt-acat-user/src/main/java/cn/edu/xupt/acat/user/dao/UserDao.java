package cn.edu.xupt.acat.user.dao;

import cn.edu.xupt.acat.user.domain.entity.TbUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<TbUser> {
}
