package cn.edu.xupt.acat.user.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.user.domain.entity.TbUser;
import cn.edu.xupt.acat.user.domain.vo.UserSearchVo;
import cn.edu.xupt.acat.user.service.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserSearchController {

    @Autowired
    private UserSearchService userSearchService;

    @PostMapping("/query")
    public R query(@RequestBody TbUser user){
        return R.ok().put("data", userSearchService.query(user));
    }

    @PostMapping("/search")
    public R search(@RequestBody UserSearchVo vo){
        return R.ok().put("data", userSearchService.search(vo));
    }
}
