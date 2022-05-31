package cn.edu.xupt.acat.report.config;

import cn.edu.xupt.acat.report.task.ReportThread;
import cn.edu.xupt.acat.report.task.handler.ReportTaskFailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource(value = {"classpath:ThreadPoolExecutor.properties"})
public class ThreadPoolExecutorConfig {

    @Value("${REPORT_THREAD_POOL_MAX_THREAD}")
    private int maxPoolSize;

    @Value("${REPORT_THREAD_POOL_MAX_THREAD}")
    private int maxQueueSize;

    @Autowired
    private ReportTaskFailHandler handler;

    @Bean
    public ThreadPoolExecutor createThreadPoolExecutor() {

        //获取CPU核心数
        int processorNumber = Runtime.getRuntime().availableProcessors();
        //IO密集型任务
        int corePoolSize = 2 * processorNumber;

        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(maxQueueSize),
                handler);
    }
}
