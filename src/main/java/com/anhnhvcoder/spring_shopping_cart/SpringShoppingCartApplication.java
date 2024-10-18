package com.anhnhvcoder.spring_shopping_cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SpringShoppingCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringShoppingCartApplication.class, args);
	}

}
