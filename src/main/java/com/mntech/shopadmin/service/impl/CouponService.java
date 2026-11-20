package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.CouponMapper;
import com.mntech.shopadmin.dao.UserCouponMapper;
import com.mntech.shopadmin.dto.CouponRequest;
import com.mntech.shopadmin.dto.response.CouponResponse;
import com.mntech.shopadmin.dto.response.UserCouponResponse;
import com.mntech.shopadmin.entity.CouponModel;
import com.mntech.shopadmin.entity.UserCouponModel;
import com.mntech.shopadmin.enums.CouponStatusEnum;
import com.mntech.shopadmin.enums.CouponTypeEnum;
import com.mntech.shopadmin.enums.UserCouponStatusEnum;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 优惠券服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    // ==================== 管理端 ====================

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        // 1. 参数校验
        validateCouponRequest(request);

        // 2. 构建实体
        CouponModel coupon = new CouponModel();
        BeanUtils.copyProperties(request, coupon);
        applyDiscountValue(coupon, request.getType(), request.getDiscountValue());
        coupon.setRemainingQuantity(request.getTotalQuantity());
        coupon.setStatus(CouponStatusEnum.ACTIVE.getCode());
        coupon.setCreatedBy("admin");

        // 3. 保存
        couponMapper.insert(coupon);

        log.info("创建优惠券成功: {}", coupon.getName());
        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(String id, CouponRequest request) {
        // 1. 查询是否存在
        CouponModel coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 2. 上架中且已被用户领取的不能修改
        if (CouponStatusEnum.ACTIVE.getCode().equals(coupon.getStatus())) {
            LambdaQueryWrapper<UserCouponModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCouponModel::getCouponId, id);
            Long count = userCouponMapper.selectCount(wrapper);
            if (count != null && count > 0) {
                throw new BusinessException("该优惠券已上架且已被用户领取，不能修改，请先下架");
            }
        }

        // 3. 参数校验
        validateCouponRequest(request);

        // 4. 更新
        BeanUtils.copyProperties(request, coupon, "id", "createdBy", "createdAt", "status");
        applyDiscountValue(coupon, request.getType(), request.getDiscountValue());
        if (request.getTotalQuantity() != null && coupon.getTotalQuantity() != null
                && !request.getTotalQuantity().equals(coupon.getTotalQuantity())) {
            // 如果发行量变更，调整剩余量
            int diff = request.getTotalQuantity() - coupon.getTotalQuantity();
            Integer remaining = coupon.getRemainingQuantity() == null ? 0 : coupon.getRemainingQuantity();
            coupon.setRemainingQuantity(Math.max(0, remaining + diff));
        }
        couponMapper.updateById(coupon);

        log.info("更新优惠券成功: {}", coupon.getName());
        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(String id) {
        CouponModel coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }

        // 上架中且已被用户领取的不能删除
        if (CouponStatusEnum.ACTIVE.getCode().equals(coupon.getStatus())) {
            // 检查是否已被领取
            LambdaQueryWrapper<UserCouponModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCouponModel::getCouponId, id)
                    .eq(UserCouponModel::getStatus, UserCouponStatusEnum.UNUSED.getCode());
            Long count = userCouponMapper.selectCount(wrapper);
            if (count != null && count > 0) {
                throw new BusinessException("该优惠券已被用户领取，不能删除，请先下架");
            }
        }

        couponMapper.deleteById(id);
        log.info("删除优惠券成功: {}", coupon.getName());
    }

    @Override
    public IPage<CouponModel> listCoupons(Integer page, Integer size, String name, String status) {
        LambdaQueryWrapper<CouponModel> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(CouponModel::getDeleted, 0);

        if (StringUtils.hasText(name)) {
            wrapper.like(CouponModel::getName, name);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(CouponModel::getStatus, status);
        }
        wrapper.orderByDesc(CouponModel::getCreatedAt);

        Page<CouponModel> pageParam = new Page<>(page, size);
        return couponMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public CouponResponse getCouponDetail(String id) {
        CouponModel coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        return convertToResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse changeStatus(String id, String status) {
        CouponModel coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!CouponStatusEnum.ACTIVE.getCode().equals(status)
                && !CouponStatusEnum.INACTIVE.getCode().equals(status)) {
            throw new BusinessException("不支持的状态: " + status);
        }
        coupon.setStatus(status);
        couponMapper.updateById(coupon);
        log.info("优惠券上下架成功: {} -> {}", coupon.getName(), status);
        return convertToResponse(coupon);
    }

    /**
     * 根据类型把请求的优惠值写入 value / discount 字段
     */
    private void applyDiscountValue(CouponModel coupon, String type, BigDecimal discountValue) {
        if (CouponTypeEnum.DISCOUNT.getCode().equals(type)) {
            coupon.setDiscount(discountValue);
            coupon.setValue(null);
        } else if (CouponTypeEnum.CASH.getCode().equals(type)) {
            coupon.setValue(discountValue);
            coupon.setDiscount(null);
        } else {
            coupon.setValue(null);
            coupon.setDiscount(null);
        }
    }

    /**
     * 校验优惠券请求参数
     */
    private void validateCouponRequest(CouponRequest request) {
        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("有效期开始时间不能晚于结束时间");
        }

        String type = request.getType() == null ? "" : request.getType().toLowerCase();
        if (CouponTypeEnum.DISCOUNT.getCode().equals(type)) {
            if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0
                    || request.getDiscountValue().compareTo(BigDecimal.TEN) > 0) {
                throw new BusinessException("折扣值必须在0-10之间");
            }
        }

        if (CouponTypeEnum.CASH.getCode().equals(type)) {
            if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("满减金额必须大于0");
            }
            if (request.getMinAmount() != null
                    && request.getDiscountValue().compareTo(request.getMinAmount()) > 0) {
                throw new BusinessException("满减金额不能大于门槛金额");
            }
        }
    }

    /**
     * 转换为响应对象
     */
    private CouponResponse convertToResponse(CouponModel coupon) {
        CouponResponse response = new CouponResponse();
        BeanUtils.copyProperties(coupon, response);
        response.setTypeDesc(coupon.getTypeDesc());
        response.setStatusDesc(coupon.getStatusDesc());
        return response;
    }

    /**
     * 转换为用户优惠券响应
     */
    private UserCouponResponse convertToUserCouponResponse(UserCouponModel userCoupon, CouponModel coupon) {
        UserCouponResponse response = new UserCouponResponse();
        response.setId(userCoupon.getId());
        response.setCouponId(userCoupon.getCouponId());
        response.setStatus(userCoupon.getStatus());
        response.setStatusDesc(userCoupon.getStatusDesc());
        response.setEndTime(userCoupon.getEndTime());
        response.setReceivedAt(userCoupon.getCreatedAt());
        response.setUsedTime(userCoupon.getUsedTime());

        if (coupon != null) {
            response.setCouponName(coupon.getName());
            response.setCouponType(coupon.getType());
            response.setCouponTypeDesc(coupon.getTypeDesc());
            response.setDiscountValue(coupon.getDiscountValue());
            response.setMinAmount(coupon.getMinAmount());
            response.setMaxDiscount(coupon.getMaxDiscount());
            response.setApplicableScope(coupon.getApplicableScope());
            response.setApplicableIds(coupon.getApplicableIds());
        }

        return response;
    }
}