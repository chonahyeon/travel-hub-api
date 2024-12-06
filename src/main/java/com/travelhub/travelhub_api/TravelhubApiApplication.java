package com.travelhub.travelhub_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class TravelhubApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelhubApiApplication.class, args);
	}

}
