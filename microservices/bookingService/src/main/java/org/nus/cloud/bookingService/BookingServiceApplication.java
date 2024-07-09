package org.nus.cloud.bookingService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.bookingService.mapper")
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BookingServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
