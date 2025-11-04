package com.khangdjnh.edu_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableFeignClients
public class EduAppApplication {

	private static final Logger log = LoggerFactory.getLogger(EduAppApplication.class);

	private final Environment env;

	public EduAppApplication(Environment env) {
		this.env = env;
	}

	public static void main(String[] args) {
		SpringApplication.run(EduAppApplication.class, args);
	}

	// Khi ứng dụng khởi động xong
	@EventListener(ApplicationReadyEvent.class)
	public void logWhenReady() {
		String port = env.getProperty("server.port", "8080");
		String contextPath = env.getProperty("server.servlet.context-path", "");
		log.info("✅ Server đã được bật thành công tại: http://localhost:{}{}", port, contextPath);
	}
}
