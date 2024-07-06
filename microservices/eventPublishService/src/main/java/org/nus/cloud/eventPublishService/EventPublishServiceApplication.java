package org.nus.cloud.eventPublishService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.eventPublishService.mapper")
public class EventPublishServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EventPublishServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
