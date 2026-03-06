package com.vibe.vibefeedservice;

import com.vibe.vibefeedservice.messaging.PostEventStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableBinding(PostEventStream.class)
@EnableFeignClients
public class VibeFeedServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibeFeedServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
