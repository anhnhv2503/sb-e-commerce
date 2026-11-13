package com.anhnhvcoder.ecommerce.service.Impl;

import com.anhnhvcoder.ecommerce.exception.AlreadyExistedException;
import com.anhnhvcoder.ecommerce.exception.ResourceNotFoundException;
import com.anhnhvcoder.ecommerce.model.Brand;
import com.anhnhvcoder.ecommerce.repository.BrandRepository;
import com.anhnhvcoder.ecommerce.repository.ProductRepository;
import com.anhnhvcoder.ecommerce.request.BrandRequest;
import com.anhnhvcoder.ecommerce.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Override
    public Brand createBrand(BrandRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Brand name is required");
        }
        if(brandRepository.existsByNameIgnoreCase(request.getName())){
            throw new AlreadyExistedException("Brand already exists");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogo(request.getLogo());

        return brandRepository.save(brand);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brand getBrandById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
    }

    @Override
    public Brand updateBrand(int id, BrandRequest request) {
        Brand brand = getBrandById(id);
        if (StringUtils.hasText(request.getName()) && !request.getName().equalsIgnoreCase(brand.getName())) {
            if (brandRepository.existsByNameIgnoreCase(request.getName())) {
                throw new AlreadyExistedException("Brand already exists");
            }
            brand.setName(request.getName());
        }
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        if (request.getLogo() != null) {
            brand.setLogo(request.getLogo());
        }
        return brandRepository.save(brand);
    }

    @Override
    public void deleteBrand(int id) {
        Brand brand = getBrandById(id);
        if (productRepository.existsByBrandId(id)) {
            throw new IllegalStateException("Brand is used by existing products and cannot be deleted");
        }
        brandRepository.delete(brand);
    }
}
