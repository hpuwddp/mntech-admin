package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.CommissionRecordModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface CommissionRecordMapper extends BaseMapper<CommissionRecordModel> {

    /**
     * 佣金记录分页（关联买家昵称/收益用户昵称）
     */
    @Select("<script>" +
            "SELECT c.*, ub.nickname AS buyer_nickname, uu.nickname AS user_nickname " +
            "FROM commission_record c " +
            "LEFT JOIN users ub ON ub.id = c.buyer_user_id " +
            "LEFT JOIN users uu ON uu.id = c.user_id " +
            "WHERE c.deleted = 0 " +
            "<if test='orderNo != null and orderNo != \"\"'>AND c.order_no LIKE CONCAT('%', #{orderNo}, '%')</if> " +
            "<if test='userId != null and userId != \"\"'>AND c.user_id = #{userId}</if> " +
            "<if test='status != null and status != \"\" and status != \"ALL\"'>AND c.status = #{status}</if> " +
            "ORDER BY c.created_at DESC" +
            "</script>")
    IPage<CommissionRecordModel> selectPageWithJoin(Page<?> page,
                                                    @Param("orderNo") String orderNo,
                                                    @Param("userId") String userId,
                                                    @Param("status") String status);

    /**
     * 按状态汇总佣金金额
     */
    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM commission_record WHERE deleted = 0 AND status = #{status}")
    BigDecimal sumAmountByStatus(@Param("status") String status);

    /**
     * 提现审核通过：关联佣金作废锁定状态 -> 已提现
     */
    @Update("UPDATE commission_record SET status = 'WITHDRAWN', updated_at = NOW() " +
            "WHERE deleted = 0 AND withdrawal_id = #{withdrawalId} AND status = 'LOCKED'")
    int markWithdrawnByWithdrawalId(@Param("withdrawalId") String withdrawalId);

    /**
     * 提现审核拒绝：关联佣金退回可提现
     */
    @Update("UPDATE commission_record SET status = 'AVAILABLE', withdrawal_id = NULL, updated_at = NOW() " +
            "WHERE deleted = 0 AND withdrawal_id = #{withdrawalId} AND status = 'LOCKED'")
    int unlockByWithdrawalId(@Param("withdrawalId") String withdrawalId);
}