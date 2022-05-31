package cn.edu.xupt.acat.flowcontrol.controller;

import cn.edu.xupt.acat.flowcontrol.service.FlowReceiveService;
import cn.edu.xupt.acat.lib.model.FlowReceiveInput;
import cn.edu.xupt.acat.lib.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flow")
public class FlowReceiveController {

    @Autowired
    private FlowReceiveService flowReceiveService;

    @PostMapping("/receive")
    public R receive(@RequestBody FlowReceiveInput input) {
        flowReceiveService.receive(input);
        return R.ok();
    }
}
