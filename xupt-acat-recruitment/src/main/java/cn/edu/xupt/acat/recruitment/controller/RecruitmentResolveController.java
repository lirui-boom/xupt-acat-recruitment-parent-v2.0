package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.service.RecruitmentResolveService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentResolveController {

    @Resource
    private RecruitmentResolveService recruitmentResolveService;

    @PostMapping("/resolve/{nid}/{status}/{opUser}")
    public R resolve(@PathVariable("nid") String nid, @PathVariable("status") int status,
                     @PathVariable("opUser") String opUser) {
        return recruitmentResolveService.resolve(nid , status, opUser);
    }
}
