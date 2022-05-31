package cn.edu.xupt.acat.flowcontrol.controller;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.service.FlowEsDumpService;
import cn.edu.xupt.acat.lib.response.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/flow")
public class FlowDumpToEsController {

    @Resource
    private FlowEsDumpService flowEsDumpService;

    @PostMapping("/dump")
    public R dump(@RequestBody FlowEsEntity entity){
        return flowEsDumpService.dumpToEs(entity);
    }
}
