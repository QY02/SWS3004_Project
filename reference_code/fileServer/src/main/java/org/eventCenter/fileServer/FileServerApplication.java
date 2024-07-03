package org.eventCenter.fileServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class FileServerApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(FileServerApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
		springApplication.run(args);
	}

}
