package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.CategoryRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.entity.CategoryModel;
import com.mntech.shopadmin.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    /**
     * 获取分类树（所有分类树形结构）
     */
    @GetMapping("/tree")
    public ApiResponse<List<CategoryModel>> getCategoryTree() {
        List<CategoryModel> tree = categoryService.getCategoryTree();
        return ApiResponse.success("获取分类树成功", tree);
    }

    /**
     * 获取分类列表（平铺）
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getCategories(
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        IPage<CategoryModel> categoryPage = categoryService.getCategories(parentId, name, status, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("list", categoryPage.getRecords());
        response.put("total", categoryPage.getTotal());
        response.put("page", categoryPage.getCurrent());
        response.put("pageSize", categoryPage.getSize());
        response.put("totalPages", categoryPage.getPages());

        return ApiResponse.success("获取分类列表成功", response);
    }

    /**
     * 获取子分类列表
     */
    @GetMapping("/children/{parentId}")
    public ApiResponse<List<CategoryModel>> getChildren(@PathVariable String parentId) {
        List<CategoryModel> children = categoryService.getChildren(parentId);
        return ApiResponse.success("获取子分类成功", children);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryModel> getCategory(@PathVariable String id) {
        CategoryModel category = categoryService.getCategoryById(id);
        return ApiResponse.success("获取分类成功", category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public ApiResponse<CategoryModel> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryModel category = categoryService.createCategory(request);
        return ApiResponse.success("创建分类成功", category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ApiResponse<CategoryModel> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryModel category = categoryService.updateCategory(id, request);
        return ApiResponse.success("更新分类成功", category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("删除分类成功");
    }

    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        categoryService.updateStatus(id, status);
        return ApiResponse.success("更新状态成功");
    }
}