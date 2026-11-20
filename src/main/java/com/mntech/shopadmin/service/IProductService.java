package com.mntech.shopadmin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.ProductCreateRequest;
import com.mntech.shopadmin.dto.ProductUpdateRequest;
import com.mntech.shopadmin.dto.response.ProductResponse;
import com.mntech.shopadmin.entity.ProductModel;

public interface IProductService {
    IPage<ProductModel> getProducts(String search, String status, Integer page, Integer limit);
    ProductResponse createProduct(ProductCreateRequest request);
    ProductModel updateProduct(String id, ProductUpdateRequest request);
    void deleteProduct(String id);
    ProductResponse getProductById(String id);
}
