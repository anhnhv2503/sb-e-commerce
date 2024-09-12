package com.anhnhvcoder.spring_shopping_cart.service.Impl;

import com.anhnhvcoder.spring_shopping_cart.exception.ResourceNotFoundException;
import com.anhnhvcoder.spring_shopping_cart.model.Category;
import com.anhnhvcoder.spring_shopping_cart.model.Product;
import com.anhnhvcoder.spring_shopping_cart.model.ProductImages;
import com.anhnhvcoder.spring_shopping_cart.model.Size;
import com.anhnhvcoder.spring_shopping_cart.repository.CategoryRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductImagesRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.ProductRepository;
import com.anhnhvcoder.spring_shopping_cart.repository.SizeRepository;
import com.anhnhvcoder.spring_shopping_cart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImagesRepository productImagesRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final SizeRepository sizeRepository;

    @Override
    public Product addProduct(String name,
                              String brand,
                              String description,
                              BigDecimal price,
                              int inventory,
                              Long categoryId,
                              Long sizeId,
                              MultipartFile[] images) throws IOException {
        Product product = new Product();
        product.setName(name);
        product.setBrand(brand);
        product.setDescription(description);
        product.setPrice(price);
//        product.setInventory(inventory);

        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new ResourceNotFoundException("Size not found"));
        size.setQuantity(inventory);
        product.setSize(Set.of(size));
        size.setProduct(product);
        sizeRepository.save(size);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setCategory(category);

        List<ProductImages> listImages = new ArrayList<>();

        for (MultipartFile image: images){
            Map r = cloudinaryService.upload(image);
            String url = (String) r.get("url");

            ProductImages productImages = new ProductImages();
            productImages.setUrl(url);
            productImages.setProduct(product);
            listImages.add(productImages);
        }
        product.setImages(listImages);
        productRepository.save(product);

        for (ProductImages productImages: listImages){
            productImagesRepository.save(productImages);
        }

        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete, () -> {
            throw new ResourceNotFoundException("Product not found");
        });
    }

    @Override
    public Product updateProduct(Long id,
                                 String name,
                                 String brand,
                                 String description,
                                 BigDecimal price,
                                 int inventory,
                                 Long categoryId,
                                 Long sizeId
                                 ) throws IOException {
        Product updatingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            updatingProduct.setId(id);
            updatingProduct.setName(name);
            updatingProduct.setBrand(brand);
            updatingProduct.setDescription(description);
            updatingProduct.setPrice(price);
//            updatingProduct.setInventory(inventory);
            Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new ResourceNotFoundException("Size not found"));
            size.setQuantity(inventory);
            updatingProduct.setSize(Set.of(size));
            size.setProduct(updatingProduct);
            sizeRepository.save(size);

            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            updatingProduct.setCategory(category);

            productRepository.save(updatingProduct);


            return updatingProduct;
    }

    @Override
    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Long countProducts() {
        return productRepository.count();
    }

}
