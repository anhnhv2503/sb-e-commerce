package com.anhnhvcoder.spring_shopping_cart;

import com.anhnhvcoder.spring_shopping_cart.model.ProductImages;
import com.anhnhvcoder.spring_shopping_cart.model.User;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductImagesRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringShoppingCartApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		User user = userRepository.findByEmail("tiennhv2812@gmail.com");
		System.out.println(user);
	}

}
