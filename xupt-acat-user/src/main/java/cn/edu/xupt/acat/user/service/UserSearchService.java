package cn.edu.xupt.acat.user.service;

import cn.edu.xupt.acat.lib.response.SearchPage;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.domain.vo.UserSearchVo;

import java.util.List;

public interface UserSearchService {
    public abstract List<TbUser> query(TbUser user);
    public abstract SearchPage<List<TbUser>> search(UserSearchVo vo);
}
