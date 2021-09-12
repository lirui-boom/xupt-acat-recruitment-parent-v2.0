package cn.edu.xupt.acat.flowcontrol.controller;

import cn.edu.xupt.acat.flowcontrol.domain.entity.FlowEsEntity;
import cn.edu.xupt.acat.flowcontrol.domain.vo.FlowEsEntityVo;
import cn.edu.xupt.acat.flowcontrol.service.FlowSearchService;
import cn.edu.xupt.acat.lib.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flow")
public class FlowSearchController {

    @Autowired
    private FlowSearchService flowSearchService;

    @PostMapping("/search")
    public R search(@RequestBody FlowEsEntityVo vo) {
        return R.ok().put("data", flowSearchService.search(vo));
    }

    @PostMapping("/query")
    public R query(@RequestBody FlowEsEntity entity) {
        return R.ok().put("data", flowSearchService.query(entity));
    }
}
