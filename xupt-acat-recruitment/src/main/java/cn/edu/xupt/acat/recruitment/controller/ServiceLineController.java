package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.service.ServiceLineAddService;
import cn.edu.xupt.acat.recruitment.service.ServiceLineUpdateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/service_line")
public class ServiceLineController {

    @Resource
    private ServiceLineAddService serviceLineAddService;

    @Resource
    private ServiceLineUpdateService serviceLineUpdateService;

    @PostMapping("/add")
    public R add(@RequestBody TbServiceLine serviceLine) {
        return R.ok().put("data", serviceLineAddService.addServiceLine(serviceLine));
    }

    @PutMapping("/update")
    public R update(@RequestBody TbServiceLine serviceLine) {
        return serviceLineUpdateService.update(serviceLine);
    }
}
