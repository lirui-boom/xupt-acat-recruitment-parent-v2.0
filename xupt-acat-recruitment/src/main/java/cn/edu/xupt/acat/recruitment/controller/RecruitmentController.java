package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.service.RecruitmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentController {

    @Resource
    private RecruitmentService recruitmentService;

    @GetMapping("/getNid/{username}")
    public R getNid(@PathVariable("username") String username) {
        return R.ok().put("data", recruitmentService.getNid(username));
    }

    @GetMapping("/getRecruitment/{nid}/{workType}")
    public R getRecruitment(@PathVariable("nid") String nid, @PathVariable("workType") String workType) {
        return R.ok().put("data", recruitmentService.getRecruitment(nid, workType));
    }
}
