package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.model.*;
import com.anhnhvcoder.ecommerce.repository.*;
import com.anhnhvcoder.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final BrandRepository brandRepository;

    @Override
    public Product addProduct(String name,
                              String brand,
                              String description,
                              BigDecimal price,
                              int inventory,
                              String categoryName,
                              String sizeName,
                              MultipartFile[] images) throws IOException {
        Brand brandEntity = brandRepository.findByName(brand)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        Product product = new Product();
        product.setName(name);
        product.setBrand(brandEntity);
        product.setDescription(description);
        product.setPrice(price);

        Size size = new Size();
        size.setSizeName(sizeName);
        size.setQuantity(inventory);
        product.setSize(Set.of(size));
        size.setProduct(product);


        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setCategory(category);

        List<ProductImages> listImages = new ArrayList<>();

        for (MultipartFile image : images) {
            Map r = cloudinaryService.upload(image);
            String url = (String) r.get("url");

            ProductImages productImages = new ProductImages();
            productImages.setUrl(url);
            productImages.setProduct(product);
            listImages.add(productImages);
        }
        product.setImages(listImages);
        productRepository.save(product);
        sizeRepository.save(size);

        for (ProductImages productImages : listImages) {
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
        return productRepository.findWithDetailsById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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

        Brand brandEntity = brandRepository.findByName(brand)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        Product updatingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        updatingProduct.setId(id);
        updatingProduct.setName(name);
        updatingProduct.setBrand(brandEntity);
        updatingProduct.setDescription(description);
        updatingProduct.setPrice(price);

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
        return null;
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Long countProducts() {
        return productRepository.count();
    }

    @Override
    public Size addMoreSize(Long productId, String sizeName, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Size existedSize = sizeRepository.findBySizeNameAndProductId(sizeName, productId);
        if (existedSize != null) {
            existedSize.setQuantity(existedSize.getQuantity() + quantity);
            sizeRepository.save(existedSize);
            return existedSize;
        } else {
            Size size = new Size();
            size.setSizeName(sizeName);
            size.setQuantity(quantity);
            size.setProduct(product);
            sizeRepository.save(size);
            return size;
        }

    }

    @Override
    public List<Product> getNewArrivalProducts() {
        return productRepository.findAllByOrderByIdDesc(Pageable.ofSize(8));
    }

    @Override
    public List<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    @Override
    public int getInventoryBySizeId(Long sizeId) {
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new ResourceNotFoundException("Size not found"));
        return size.getQuantity();
    }

    @Override
    public Size getProductBySizeId(Long sizeId) {
        return sizeRepository.findById(sizeId).orElseThrow(() -> new ResourceNotFoundException("Size not found"));
    }

    @Override
    public Size editSizeInventory(Long sizeId, int quantity) {
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new ResourceNotFoundException("Size not found"));
        size.setQuantity(quantity);
        return sizeRepository.save(size);
    }

    @Override
    public Page<Product> getProductsByPage(
            String categoryName,
            String brandName,
            String search,
            Pageable pageable) {
        if (search == null)
            search = "";

        if (categoryName == null)
            categoryName = "";

        if (brandName == null)
            brandName = "";

        return productRepository.searchProducts(pageable, search, categoryName, brandName);
    }

}
