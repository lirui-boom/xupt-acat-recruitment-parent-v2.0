package cn.edu.xupt.acat.report.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.report.domain.entity.TbReport;
import cn.edu.xupt.acat.report.service.ReportSubmitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/report")
public class ReportSubmitController {

    @Resource
    private ReportSubmitService reportSubmitService;

    @PostMapping("/submit")
    public R submit(@RequestBody TbReport report) {
        return reportSubmitService.submit(report);
    }
}
