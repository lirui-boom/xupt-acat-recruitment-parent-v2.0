package cn.edu.xupt.acat.apply.controller;

import cn.edu.xupt.acat.apply.service.ApplyReceiveService;
import cn.edu.xupt.acat.lib.model.FlowSendInput;
import cn.edu.xupt.acat.lib.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apply")
public class ApplyReceiveController {

    @Autowired
    private ApplyReceiveService applyReceiveService;

    @PostMapping("/receive")
    public R receive(@RequestBody FlowSendInput input) {
        return applyReceiveService.receive(input);
    }
}
