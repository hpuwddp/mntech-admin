package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.CategoryRequest;
import com.mntech.shopadmin.entity.CategoryModel;

import java.util.List;

public interface ICategoryService {
    List<CategoryModel> getCategoryTree();
    IPage<CategoryModel> getCategories(String parentId, String name, String status, Integer page, Integer limit);
    List<CategoryModel> getChildren(String parentId);
    CategoryModel getCategoryById(String id);
    CategoryModel createCategory(CategoryRequest request);
    CategoryModel updateCategory(String id, CategoryRequest request);
    void deleteCategory(String id);
    void updateStatus(String id, String status);
}
