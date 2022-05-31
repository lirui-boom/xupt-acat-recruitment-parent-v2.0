package cn.edu.xupt.acat.report.config;


import cn.edu.xupt.acat.report.service.ReportTaskService;
import cn.edu.xupt.acat.report.task.ReportThread;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Configuration
@EnableScheduling
public class ReportScheduleTask {

    @Resource
    private ReportTaskService reportTaskService;

    //直接指定时间间隔，每10秒执行一次
    @Scheduled(fixedRate = 20000)
    private void configureTasks() {
        reportTaskService.execute();
    }
}
