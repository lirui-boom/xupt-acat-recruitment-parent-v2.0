package cn.edu.xupt.acat.auth;

import cn.edu.xupt.acat.auth.config.RsaConfiguration;
import cn.edu.xupt.acat.lib.util.BCryptUtil;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@EnableConfigurationProperties({RsaConfiguration.class})
//@ComponentScan(basePackages = {"cn.edu.xupt.acat.auth","cn.edu.xupt.acat.lib"}) // ExceptionHandler
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
