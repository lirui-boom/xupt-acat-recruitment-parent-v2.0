package cn.edu.xupt.acat.auth.service.impl;

import cn.edu.xupt.acat.auth.service.AuthUserService;
import cn.edu.xupt.acat.user.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userService.loadUserByUsername(s);
    }
}
