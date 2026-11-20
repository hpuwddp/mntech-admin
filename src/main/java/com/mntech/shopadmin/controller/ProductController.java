package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.ProductCreateRequest;
import com.mntech.shopadmin.dto.ProductUpdateRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.ProductResponse;
import com.mntech.shopadmin.entity.ProductModel;
import com.mntech.shopadmin.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        IPage<ProductModel> productPage = productService.getProducts(search, status, page, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getRecords());
        response.put("total", productPage.getTotal());
        response.put("page", productPage.getCurrent());
        response.put("totalPages", productPage.getPages());
        response.put("size", productPage.getSize());
        return ApiResponse.success("获取商品列表成功", response);
    }
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ApiResponse.success("商品创建成功", product);
    }
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ApiResponse.success("商品创建成功", product);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductModel> updateProduct(@PathVariable String id, @Valid @RequestBody ProductUpdateRequest request) {
        ProductModel product = productService.updateProduct(id, request);
        return ApiResponse.success("商品更新成功", product);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success("商品删除成功");
    }
}