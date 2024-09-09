package com.anhnhvcoder.spring_shopping_cart;

import com.anhnhvcoder.spring_shopping_cart.model.ProductImages;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductImagesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringShoppingCartApplicationTests {

	@Autowired
	private ProductImagesRepository productImagesRepository;

	@Test
	void contextLoads() {
		List<ProductImages> productImages = productImagesRepository.findByProductId(1L);

		for (ProductImages productImage : productImages) {
			System.out.println(productImage.getUrl());
		}
	}

}
