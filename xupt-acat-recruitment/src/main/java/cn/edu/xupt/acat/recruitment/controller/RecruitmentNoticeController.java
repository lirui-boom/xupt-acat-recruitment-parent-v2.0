package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.vo.SendNoticeVo;
import cn.edu.xupt.acat.recruitment.service.RecruitmentNoticeService;
import cn.edu.xupt.acat.recruitment.service.RecruitmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentNoticeController {

    @Resource
    private RecruitmentNoticeService recruitmentNoticeService;

    @PostMapping("/notice")
    public R notice(@RequestBody SendNoticeVo input) {
        recruitmentNoticeService.notice(input);
        return R.ok();
    }
}
