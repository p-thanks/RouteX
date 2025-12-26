package com.delivery.RouteX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories("com.delivery.RouteX.Repository")
@EnableAsync
@EnableScheduling
public class RouteXApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteXApplication.class, args);
	}

}
