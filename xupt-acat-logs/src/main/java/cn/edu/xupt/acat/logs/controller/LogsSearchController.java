package cn.edu.xupt.acat.logs.controller;

import cn.edu.xupt.acat.lib.response.R;
import cn.edu.xupt.acat.logs.domain.entity.TbLogs;
import cn.edu.xupt.acat.logs.domain.vo.LogsSearchVo;
import cn.edu.xupt.acat.logs.service.LogsSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/logs")
public class LogsSearchController {

    @Resource
    private LogsSearchService logsSearchService;

    @PostMapping("/search")
    public R search(@RequestBody LogsSearchVo vo) {
        return R.ok().put("data",logsSearchService.search(vo));
    }

    @PostMapping("/query")
    public R query(@RequestBody TbLogs logs) {
        return R.ok().put("data",logsSearchService.query(logs));
    }
}
