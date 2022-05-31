package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.entity.TbEvaluate;
import cn.edu.xupt.acat.recruitment.service.RecruitmentSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentSubmitController {

    @Autowired
    private RecruitmentSubmitService recruitmentSubmitService;

    @PostMapping("/submit")
    public R submit(@RequestBody TbEvaluate evaluate) {
        return recruitmentSubmitService.submit(evaluate);
    }
}
