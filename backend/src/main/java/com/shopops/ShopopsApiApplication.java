package com.shopops;

import com.shopops.config.ShopifyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShopifyProperties.class)
public class ShopopsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopopsApiApplication.class, args);
	}

}
