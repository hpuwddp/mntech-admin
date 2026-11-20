package com.mntech.shopadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mntech.shopadmin.dao.BrandMapper;
import com.mntech.shopadmin.dao.CategoryMapper;
import com.mntech.shopadmin.dao.ProductMapper;
import com.mntech.shopadmin.dao.SkuMapper;
import com.mntech.shopadmin.dto.ProductCreateRequest;
import com.mntech.shopadmin.dto.ProductUpdateRequest;
import com.mntech.shopadmin.dto.SkuCreateRequest;
import com.mntech.shopadmin.dto.SkuUpdateRequest;
import com.mntech.shopadmin.dto.response.ProductResponse;
import com.mntech.shopadmin.dto.response.SkuResponse;
import com.mntech.shopadmin.entity.BrandModel;
import com.mntech.shopadmin.entity.CategoryModel;
import com.mntech.shopadmin.entity.ProductModel;
import com.mntech.shopadmin.entity.SkuModel;
import com.mntech.shopadmin.enums.AuditStatus;
import com.mntech.shopadmin.enums.ProductStatus;
import com.mntech.shopadmin.enums.SkuStatus;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.IProductService;
import com.mntech.shopadmin.utils.PgArrayUtil;
import com.mntech.shopadmin.utils.SnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    private final SkuMapper skuMapper;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Override
    public IPage<ProductModel> getProducts(String search, String status, Integer page, Integer limit) {
        // 创建分页对象
        Page<ProductModel> pageParam = new Page<>(page, limit);

        // 构建查询条件
        LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();

        // 搜索条件
//        if (StringUtils.hasText(search)) {
//            wrapper.and(w -> w
//                    .like(ProductModel::getName, search)
//                    .or()
//                    .like(ProductModel::getCategoryId, search)
//            );
//        }
        if (StringUtils.hasText(search)) {
            wrapper.and(w -> w
                    .like(ProductModel::getName, search)
            );
        }
        // 状态条件
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProductModel::getStatus, status);
        }

        // 排序
        wrapper.orderByDesc(ProductModel::getCreatedAt);

        // 执行分页查询
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        try {
            // 1. 自动生成 SPU 编码
            String spuCode =snowflakeIdWorker.generateSpuCode();

            // 2. 检查SPU编码是否重复
            LambdaQueryWrapper<ProductModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductModel::getSpuCode, spuCode);
            if (productMapper.selectCount(wrapper) > 0) {
                spuCode = snowflakeIdWorker.generateSpuCode();
            }

            // 3. 构建产品实体
            ProductModel product = new ProductModel();
            product.setSpuCode(spuCode);
            product.setName(request.getName());
            product.setSubtitle(request.getSubtitle());
            product.setDescription(request.getDescription());

            product.setCategoryId(request.getCategoryId());

            product.setSellingPrice(request.getSellingPrice());
            product.setMemberPrice(request.getMemberPrice());
            product.setTotalStock(request.getTotalStock() != null ? request.getTotalStock() : 0);
            product.setAvailableStock(request.getTotalStock() != null ? request.getTotalStock() : 0);
            product.setLockedStock(0);
            product.setSoldStock(0);
            product.setStatus(ProductStatus.ON_SHELF.name());
            product.setAuditStatus(AuditStatus.PENDING.name());
            product.setHasSku(request.getHasSku() != null && request.getHasSku());
            product.setWeight(request.getWeight());
            //product.setShippingTemplateId(request.getShippingTemplateId());
            product.setDeliveryTime(request.getDeliveryTime());
            product.setMainImage(request.getMainImage());

            // ✅ 使用 ObjectMapper 替代 JSONUtil
//            if (CollectionUtil.isNotEmpty(request.getImages())) {
//                product.setImages(objectMapper.writeValueAsString(request.getImages()));
//            }
//            if (CollectionUtil.isNotEmpty(request.getTags())) {
//                product.setTags(objectMapper.writeValueAsString(request.getTags()));
//            }
//            if (request.getSkuTemplate() != null) {
//                product.setSkuTemplate(objectMapper.writeValueAsString(request.getSkuTemplate()));
//            }

            product.setSalesCount(0);
            product.setViewCount(0);
            product.setFavoriteCount(0);
            product.setReviewCount(0);
            product.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system");
            product.setVersion(0);
            product.setDeleted(0);

            productMapper.insert(product);
// ✅ 5. 插入 SKU 数据
            if (CollectionUtil.isNotEmpty(request.getSkuCreateRequests())) {
                for (SkuCreateRequest skuReq : request.getSkuCreateRequests()) {
                    SkuModel sku = new SkuModel();
                    sku.setProductId(product.getId());
                    sku.setSkuCode(snowflakeIdWorker.generateSkuCode());
                    sku.setSkuName(skuReq.getSkuName());
                    sku.setSellingPrice(skuReq.getSellingPrice());
                    sku.setOriginalPrice(skuReq.getSellingPrice()); // 默认等于销售价
                    sku.setTotalStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                    sku.setAvailableStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                    sku.setLockedStock(0);
                    sku.setSoldStock(0);
                    sku.setImageUrl(skuReq.getImageUrl());
                    sku.setStatus(SkuStatus.ACTIVE.name());
                    sku.setSalesCount(0);
                    sku.setVersion(0);
                    sku.setDeleted(0);

                    skuMapper.insert(sku);
                }
            }
            return getProductById(product.getId());
        } catch (Exception e) {
            //log.error("JSON序列化失败", e);
           // throw new BusinessException("数据格式错误");
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductModel updateProduct(String id, ProductUpdateRequest request) {
        // 1. 查询商品是否存在
        ProductModel product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        // 2. 保存当前 version（乐观锁需要）
        Integer currentVersion = product.getVersion();
        // 2. 更新商品基本信息
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setDescription(request.getDescription());

        product.setCategoryId(request.getCategoryId());

        product.setSellingPrice(request.getSellingPrice());
        product.setMemberPrice(request.getMemberPrice());
        product.setTotalStock(request.getTotalStock() != null ? request.getTotalStock() : 0);
        product.setAvailableStock(request.getTotalStock() != null ? request.getTotalStock() : 0);
        product.setHasSku(request.getHasSku() != null && request.getHasSku());
        product.setWeight(request.getWeight());
        product.setDeliveryTime(request.getDeliveryTime());
        product.setMainImage(request.getMainImage());


        product.setUpdatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system");
        product.setUpdatedAt(LocalDateTime.now());

        try {
            // 3. 更新商品
            productMapper.updateById(product);
            // 4. 处理 SKU 更新
            handleSkuUpdate(id, request.getSkuUpdateRequests(), request.getHasSku());
        }
        catch (Exception e){
            throw e;
        }

        // 5. 返回更新后的商品
        return productMapper.selectById(id);
    }

    /**
     * 处理 SKU 更新（增删改）
     */
    private void handleSkuUpdate(String productId, List<SkuUpdateRequest> skuRequests, Boolean hasSku) {
        // 如果 hasSku 为 false 或者 SKU 列表为空，删除所有 SKU
        if (hasSku == null || !hasSku || CollectionUtil.isEmpty(skuRequests)) {
            // 删除该商品下所有 SKU
            LambdaQueryWrapper<SkuModel> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SkuModel::getProductId, productId);
            skuMapper.delete(deleteWrapper);
            return;
        }

        // 查询现有的 SKU
        LambdaQueryWrapper<SkuModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuModel::getProductId, productId);
        List<SkuModel> existingSkus = skuMapper.selectList(wrapper);

        // 提取现有 SKU ID 列表
        Set<String> existingIds = existingSkus.stream()
                .map(SkuModel::getId)
                .collect(Collectors.toSet());

        // 提取请求中的 SKU ID 列表
        Set<String> requestIds = skuRequests.stream()
                .filter(s -> s.getId() != null && !s.getId().isEmpty())
                .map(SkuUpdateRequest::getId)
                .collect(Collectors.toSet());

        // 1. 删除不在请求中的 SKU
        for (SkuModel sku : existingSkus) {
            if (!requestIds.contains(sku.getId())) {
                skuMapper.deleteById(sku.getId());
            }
        }

        // 2. 更新或新增 SKU
        for (SkuUpdateRequest skuReq : skuRequests) {
            if (skuReq.getId() != null && !skuReq.getId().isEmpty()) {
                // 更新现有 SKU
                SkuModel sku = skuMapper.selectById(skuReq.getId());
                if (sku != null && sku.getProductId().equals(productId)) {
                    sku.setSkuName(skuReq.getSkuName());
                    sku.setSellingPrice(skuReq.getSellingPrice());
                    sku.setOriginalPrice(skuReq.getSellingPrice());
                    sku.setTotalStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                    sku.setAvailableStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                    sku.setImageUrl(skuReq.getImageUrl());
                    sku.setUpdatedAt(LocalDateTime.now());
                    skuMapper.updateById(sku);
                }
            } else {
                // 新增 SKU
                SkuModel newSku = new SkuModel();
                newSku.setProductId(productId);
                newSku.setSkuCode(snowflakeIdWorker.generateSkuCode());
                newSku.setSkuName(skuReq.getSkuName());
                newSku.setSellingPrice(skuReq.getSellingPrice() != null ? skuReq.getSellingPrice() : BigDecimal.ZERO);
                newSku.setOriginalPrice(skuReq.getSellingPrice() != null ? skuReq.getSellingPrice() : BigDecimal.ZERO);
                newSku.setTotalStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                newSku.setAvailableStock(skuReq.getTotalStock() != null ? skuReq.getTotalStock() : 0);
                newSku.setLockedStock(0);
                newSku.setSoldStock(0);
                newSku.setImageUrl(skuReq.getImageUrl());
                newSku.setStatus(SkuStatus.ACTIVE.name());
                newSku.setSalesCount(0);
                newSku.setVersion(0);
                newSku.setDeleted(0);
                newSku.setCreatedAt(LocalDateTime.now());
                newSku.setUpdatedAt(LocalDateTime.now());
                skuMapper.insert(newSku);
            }
        }
    }


    @Override
    public void deleteProduct(String id) {
        if (productMapper.selectById(id) == null) {
            throw new RuntimeException("商品不存在");
        }
        productMapper.deleteById(id);
        // 4. 删除关联的 SKU
        LambdaQueryWrapper<SkuModel> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(SkuModel::getProductId, id);
        skuMapper.delete(skuWrapper);
    }

    @Override
    public ProductResponse getProductById(String id) {
        try {
            ProductModel product = productMapper.selectById(id);
            if (product == null) {
                throw new BusinessException("产品不存在");
            }
            return convertToResponse(product);
        } catch (JsonProcessingException e) {
           // log.error("JSON反序列化失败", e);
            throw new BusinessException("数据格式错误");
        }
    }



    private ProductResponse convertToResponse(ProductModel product) throws JsonProcessingException {
        ProductResponse response = new ProductResponse();
        BeanUtil.copyProperties(product, response, "images", "tags", "skuTemplate");

        // 状态描述
        try {
            ProductStatus status = ProductStatus.valueOf(product.getStatus());
            response.setStatusDesc(status.getDescription());
        } catch (IllegalArgumentException e) {
            response.setStatusDesc(product.getStatus());
        }

        // 审核状态描述
        if (product.getAuditStatus() != null) {
            try {
                AuditStatus auditStatus = AuditStatus.valueOf(product.getAuditStatus());
                response.setAuditStatusDesc(auditStatus.getDescription());
            } catch (IllegalArgumentException e) {
                response.setAuditStatusDesc(product.getAuditStatus());
            }
        }

        // 当前价格
        BigDecimal currentPrice = product.getSellingPrice();
        if (product.getPromotionPrice() != null
                && product.getPromotionStart() != null
                && product.getPromotionEnd() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(product.getPromotionStart()) && now.isBefore(product.getPromotionEnd())) {
                currentPrice = product.getPromotionPrice();
            }
        }
        response.setCurrentPrice(currentPrice);

        // ✅ 使用 ObjectMapper 解析 JSON 字段
        if (StrUtil.isNotBlank(product.getImages())) {
            response.setImages(PgArrayUtil.parseToList(product.getImages()));
        }
        if (StrUtil.isNotBlank(product.getTags())) {
            response.setTags(PgArrayUtil.parseToList(product.getTags()));
        }
        if (StrUtil.isNotBlank(product.getSkuTemplate())) {
            response.setSkuTemplate(PgArrayUtil.parseToMap(product.getSkuTemplate()));
        }

        // 查询品牌名称
        if (StrUtil.isNotBlank(product.getBrandId())) {
            BrandModel brand = brandMapper.selectById(product.getBrandId());
            if (brand != null) {
                response.setBrandName(brand.getName());
            }
        }

        // 查询分类名称
        if (StrUtil.isNotBlank(product.getCategoryId())) {
            CategoryModel category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                response.setCategoryName(category.getName());
            }
        }

        // 查询SKU列表
        List<SkuModel> skus = skuMapper.selectByProductId(product.getId());
        if (CollectionUtil.isNotEmpty(skus)) {
            List<SkuResponse> skuResponses = new ArrayList<>();
            for (SkuModel sku : skus) {
                if (!SkuStatus.DELETED.name().equals(sku.getStatus())) {
                    skuResponses.add(convertSkuToResponse(sku));
                }
            }
            response.setSkus(skuResponses);
        }

        return response;
    }
    private SkuResponse convertSkuToResponse(SkuModel sku) throws JsonProcessingException {
        SkuResponse response = new SkuResponse();
        BeanUtil.copyProperties(sku, response,"attributes");

        // 状态描述
        try {
            SkuStatus status = SkuStatus.valueOf(sku.getStatus());
            response.setStatusDesc(status.getDescription());
        } catch (IllegalArgumentException e) {
            response.setStatusDesc(sku.getStatus());
        }

        // ✅ 使用 ObjectMapper 解析属性JSON
        if (StrUtil.isNotBlank(sku.getAttributes())) {
            response.setAttributes(PgArrayUtil.parseToMap(sku.getAttributes()));
        }

        // 当前价格
        BigDecimal currentPrice = sku.getSellingPrice();
        if (sku.getPromotionPrice() != null
                && sku.getPromotionStart() != null
                && sku.getPromotionEnd() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(sku.getPromotionStart()) && now.isBefore(sku.getPromotionEnd())) {
                currentPrice = sku.getPromotionPrice();
            }
        }
        response.setCurrentPrice(currentPrice);

        return response;
    }

}
