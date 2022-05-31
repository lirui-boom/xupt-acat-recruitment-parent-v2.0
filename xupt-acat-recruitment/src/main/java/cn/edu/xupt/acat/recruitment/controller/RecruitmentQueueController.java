package cn.edu.xupt.acat.recruitment.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.recruitment.domain.vo.QueueSearchVo;
import cn.edu.xupt.acat.recruitment.service.RecruitmentQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentQueueController {

    @Autowired
    private RecruitmentQueueService recruitmentQueueService;

    @PostMapping("/queue/sign/{nid}")
    public R sign(@PathVariable("nid") String nid) {
        return recruitmentQueueService.sign(nid);
    }

    @DeleteMapping("/queue/signUp/{nid}")
    public R signUp(@PathVariable("nid") String nid) {
        return recruitmentQueueService.signUp(nid);
    }

    @GetMapping("/queue/getCount/{serviceLine}/{version}/{turns}")
    public R getCount(@PathVariable("serviceLine")String serviceLine,@PathVariable("version") String version, @PathVariable("turns")int turns) {
        return recruitmentQueueService.getCount(serviceLine, version, turns);
    }

    @GetMapping("/queue/getTask/{nid}/{opUser}")
    public R getTask(@PathVariable("nid")String nid, @PathVariable("opUser") String opUser) {
        return recruitmentQueueService.getTask(nid, opUser);
    }

    @PostMapping("/queue/getList")
    public R getList(@RequestBody QueueSearchVo vo) {
        return R.ok().put("data", recruitmentQueueService.getList(vo));
    }

    @GetMapping("/queue/release/{nid}/{opUser}")
    public R release(@PathVariable("nid")String nid, @PathVariable("opUser")String opUser) {
        return recruitmentQueueService.release(nid,opUser);
    }
}
