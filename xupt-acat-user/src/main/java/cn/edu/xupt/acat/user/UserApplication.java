package cn.edu.xupt.acat.user;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@SpringBootApplication
//@ComponentScan(basePackages = {"cn.edu.xupt.acat.lib","cn.edu.xupt.acat.user"})
public class UserApplication {
    public static void main(String[] args) {
        //解决redis es netty冲突
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(UserApplication.class, args);
    }
}
