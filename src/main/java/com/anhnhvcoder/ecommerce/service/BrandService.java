package com.anhnhvcoder.ecommerce.service;

import com.anhnhvcoder.ecommerce.model.Brand;
import com.anhnhvcoder.ecommerce.request.BrandRequest;

import java.util.List;

public interface BrandService {

    Brand createBrand(BrandRequest request);

    List<Brand> getAllBrands();

    Brand getBrandById(int id);

    Brand updateBrand(int id, BrandRequest request);

    void deleteBrand(int id);
}
