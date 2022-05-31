package cn.edu.xupt.acat.recruitment;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@SpringBootApplication
//@ComponentScan(basePackages = {"cn.edu.xupt.acat.lib","cn.edu.xupt.acat.recruitment"})
public class RecruitmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecruitmentApplication.class, args);
    }
}
