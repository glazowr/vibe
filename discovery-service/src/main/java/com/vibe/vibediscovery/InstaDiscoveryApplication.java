package com.vibe.vibediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VibeDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibeDiscoveryApplication.class, args);
	}

}
