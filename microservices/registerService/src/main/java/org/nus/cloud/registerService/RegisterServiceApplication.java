package org.nus.cloud.registerService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.registerService.mapper")
public class RegisterServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RegisterServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
