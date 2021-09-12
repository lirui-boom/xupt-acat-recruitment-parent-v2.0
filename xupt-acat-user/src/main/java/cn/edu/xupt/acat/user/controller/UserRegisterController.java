package cn.edu.xupt.acat.user.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.user.domain.vo.UserVo;
import cn.edu.xupt.acat.user.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserRegisterController {

    @Resource
    private UserRegisterService userRegisterService;

    @PostMapping("/register")
    public R register(@RequestBody UserVo input){
        return userRegisterService.register(input);
    }

    @PostMapping("/getCheckCode")
    public R getCheckCode(@RequestBody UserVo input){
        return userRegisterService.getCheckCode(input);
    }
}
