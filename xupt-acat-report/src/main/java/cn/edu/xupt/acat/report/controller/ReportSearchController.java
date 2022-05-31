package cn.edu.xupt.acat.report.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.report.domain.vo.ReportSearchVo;
import cn.edu.xupt.acat.report.service.ReportSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/report")
public class ReportSearchController {

    @Resource
    private ReportSearchService reportSearchService;

    @PostMapping("/search")
    public R search(@RequestBody ReportSearchVo vo) {
        return R.ok().put("data", reportSearchService.search(vo));
    }
}
