package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.CategoryMapper;
import com.mntech.shopadmin.dto.CategoryRequest;
import com.mntech.shopadmin.entity.CategoryModel;
import com.mntech.shopadmin.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl  implements ICategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryModel> getCategoryTree() {
        // 查询所有未删除的分类
        LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryModel::getDeleted, 0);
        wrapper.orderByAsc(CategoryModel::getSortOrder);
        List<CategoryModel> allCategories = categoryMapper.selectList(wrapper);

        // 构建树形结构
        return buildTree(allCategories, null);
    }

    /**
     * 递归构建树形结构
     */
    private List<CategoryModel> buildTree(List<CategoryModel> allCategories, String parentId) {
        List<CategoryModel> result = new ArrayList<>();

        for (CategoryModel category : allCategories) {
            String categoryParentId = category.getParentId();
            // 如果 parentId 为 null 或空，表示一级分类
            boolean isRoot = (parentId == null || parentId.isEmpty())
                    && (categoryParentId == null || categoryParentId.isEmpty());

            // 如果当前分类的父级ID等于指定的parentId
            boolean isChild = parentId != null && !parentId.isEmpty()
                    && parentId.equals(categoryParentId);

            if (isRoot || isChild) {
                // 递归获取子分类
                List<CategoryModel> children = buildTree(allCategories, category.getId());
                category.setChildren(children);
                result.add(category);
            }
        }

        return result;
    }

    @Override
    public IPage<CategoryModel> getCategories(String parentId, String name, String status, Integer page, Integer limit) {
        Page<CategoryModel> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(parentId)) {
            wrapper.eq(CategoryModel::getParentId, parentId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(CategoryModel::getName, name);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(CategoryModel::getStatus, status);
        }

        wrapper.eq(CategoryModel::getDeleted, 0);
        wrapper.orderByAsc(CategoryModel::getSortOrder);

        return categoryMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<CategoryModel> getChildren(String parentId) {
        LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryModel::getParentId, parentId);
        wrapper.eq(CategoryModel::getDeleted, 0);
        wrapper.orderByAsc(CategoryModel::getSortOrder);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public CategoryModel getCategoryById(String id) {
        CategoryModel category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }

    @Override
    @Transactional
    public CategoryModel createCategory(CategoryRequest request) {
        // 检查编码是否重复
        if (StringUtils.hasText(request.getCode())) {
            LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CategoryModel::getCode, request.getCode());
            wrapper.eq(CategoryModel::getDeleted, 0);
            if (categoryMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("分类编码已存在");
            }
        }

        // 如果父级ID存在，检查父级是否存在
        if (StringUtils.hasText(request.getParentId())) {
            CategoryModel parent = categoryMapper.selectById(request.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                throw new RuntimeException("父级分类不存在");
            }
            // 自动设置层级
            request.setLevel(parent.getLevel() + 1);
        } else {
            // 一级分类
            request.setLevel(1);
        }

        CategoryModel category = new CategoryModel();
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setIcon(request.getIcon());
        category.setImage(request.getImage());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        category.setDeleted(0);

        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public CategoryModel updateCategory(String id, CategoryRequest request) {
        CategoryModel category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查编码是否重复（排除自己）
        if (StringUtils.hasText(request.getCode()) && !request.getCode().equals(category.getCode())) {
            LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CategoryModel::getCode, request.getCode());
            wrapper.eq(CategoryModel::getDeleted, 0);
            wrapper.ne(CategoryModel::getId, id);
            if (categoryMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("分类编码已存在");
            }
        }

        // 如果修改了父级
        if (request.getParentId() != null && !request.getParentId().equals(category.getParentId())) {
            // 不能将自己设为父级
            if (id.equals(request.getParentId())) {
                throw new RuntimeException("不能将自己设为父级分类");
            }

            // 检查父级是否存在
            if (StringUtils.hasText(request.getParentId())) {
                CategoryModel parent = categoryMapper.selectById(request.getParentId());
                if (parent == null || parent.getDeleted() == 1) {
                    throw new RuntimeException("父级分类不存在");
                }
                // 更新层级
                category.setLevel(parent.getLevel() + 1);
            } else {
                category.setLevel(1);
            }
            category.setParentId(request.getParentId());
        }

        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setIcon(request.getIcon());
        category.setImage(request.getImage());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        CategoryModel category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查是否有子分类
        LambdaQueryWrapper<CategoryModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryModel::getParentId, id);
        wrapper.eq(CategoryModel::getDeleted, 0);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, String status) {
        CategoryModel category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        category.setStatus(status);
        categoryMapper.updateById(category);
    }
}