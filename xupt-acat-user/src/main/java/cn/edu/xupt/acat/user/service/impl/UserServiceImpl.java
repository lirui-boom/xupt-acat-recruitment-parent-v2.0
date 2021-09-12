package cn.edu.xupt.acat.user.service.impl;

import cn.edu.xupt.acat.user.dao.UserDao;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        QueryWrapper<TbUser> UserQueryWrapper = new QueryWrapper<TbUser>();
        UserQueryWrapper.eq("username", s);
        List<TbUser> userList = userDao.selectList(UserQueryWrapper);

        if (userList == null || userList.size() == 0) {
            throw new UsernameNotFoundException("username:" + s);
        }

        return userList.get(0);
    }
}
