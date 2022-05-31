package cn.edu.xupt.acat.flowcontrol.controller;

import cn.edu.xupt.acat.flowcontrol.service.library.RedisFlowControl;
import cn.edu.xupt.acat.lib.response.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flow")
public class FlowRedisController {

    @Resource
    private RedisFlowControl redisFlowControl;

    @GetMapping("/redis/{flow}")
    public R flow(@PathVariable("flow") String key) {
        return R.ok().put("data", redisFlowControl.isPass(key, 3));
    }
}
