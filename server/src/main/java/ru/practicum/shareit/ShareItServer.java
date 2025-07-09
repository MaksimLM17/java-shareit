package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class ShareItServer {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ShareItServer.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "9090"));
		app.run(args);
	}

}
