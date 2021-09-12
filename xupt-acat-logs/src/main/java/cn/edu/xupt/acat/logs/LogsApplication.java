package cn.edu.xupt.acat.logs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"cn.edu.xupt.acat.lib", "cn.edu.xupt.acat.logs"})
public class LogsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogsApplication.class, args);
    }
}
