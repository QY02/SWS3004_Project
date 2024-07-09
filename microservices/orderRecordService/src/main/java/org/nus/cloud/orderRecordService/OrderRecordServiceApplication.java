package org.nus.cloud.orderRecordService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.orderRecordService.mapper")
public class OrderRecordServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(OrderRecordServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
