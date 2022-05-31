package cn.edu.xupt.acat.auth.controller;

import cn.edu.xupt.acat.lib.response.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
    @GetMapping("/success")
    public R success() {
        return R.ok();
    }
}
