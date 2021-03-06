package cn.edu.xupt.acat.process;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@SpringBootApplication
@ComponentScan(basePackages = {"cn.edu.xupt.acat.lib","cn.edu.xupt.acat.process"})
public class ProcessApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessApplication.class, args);
    }
}
