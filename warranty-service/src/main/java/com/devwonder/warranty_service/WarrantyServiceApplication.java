package com.devwonder.warranty_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@ComponentScan(basePackages = {"com.devwonder.warranty_service", "com.devwonder.common"})
@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
public class WarrantyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarrantyServiceApplication.class, args);
	}

}
