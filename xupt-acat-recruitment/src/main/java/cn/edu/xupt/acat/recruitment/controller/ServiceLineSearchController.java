package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbServiceLine;
import cn.edu.xupt.acat.recruitment.domain.vo.ServiceLineSearchVo;
import cn.edu.xupt.acat.recruitment.service.ServiceLineSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/service_line")
public class ServiceLineSearchController {
    @Resource
    private ServiceLineSearchService serviceLineSearchService;

    @PostMapping("/search")
    public R search(@RequestBody ServiceLineSearchVo vo) {
        return R.ok().put("data", serviceLineSearchService.search(vo));
    }

    @PostMapping("/query")
    public R query(@RequestBody TbServiceLine serviceLine) {
        return R.ok().put("data", serviceLineSearchService.query(serviceLine));
    }
}
