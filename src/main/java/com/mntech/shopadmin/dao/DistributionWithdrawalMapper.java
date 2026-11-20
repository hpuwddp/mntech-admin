package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.DistributionWithdrawalModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface DistributionWithdrawalMapper extends BaseMapper<DistributionWithdrawalModel> {

    /**
     * 提现单分页（关联用户昵称）
     */
    @Select("<script>" +
            "SELECT w.*, u.nickname AS user_nickname " +
            "FROM distribution_withdrawal w " +
            "LEFT JOIN users u ON u.id = w.user_id " +
            "WHERE w.deleted = 0 " +
            "<if test='status != null and status != \"\" and status != \"ALL\"'>AND w.status = #{status}</if> " +
            "<if test='userId != null and userId != \"\"'>AND w.user_id = #{userId}</if> " +
            "ORDER BY w.created_at DESC" +
            "</script>")
    IPage<DistributionWithdrawalModel> selectPageWithJoin(Page<?> page,
                                                          @Param("status") String status,
                                                          @Param("userId") String userId);

    /**
     * 待审核提现笔数
     */
    @Select("SELECT COUNT(*) FROM distribution_withdrawal WHERE deleted = 0 AND status = 'PENDING'")
    long countPending();

    /**
     * 待审核提现金额合计
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM distribution_withdrawal WHERE deleted = 0 AND status = 'PENDING'")
    BigDecimal sumPendingAmount();

    /**
     * 打款成功：更新打款状态/流水号/时间，清空失败原因
     */
    @Update("UPDATE distribution_withdrawal SET payout_status = 'SUCCESS', payout_no = #{payoutNo}, payout_time = NOW(), fail_reason = NULL, updated_at = NOW() WHERE id = #{id} AND deleted = 0")
    int markPayoutSuccess(@Param("id") String id, @Param("payoutNo") String payoutNo);

    /**
     * 打款失败：更新打款状态/失败原因
     */
    @Update("UPDATE distribution_withdrawal SET payout_status = 'FAILED', fail_reason = #{failReason}, updated_at = NOW() WHERE id = #{id} AND deleted = 0")
    int markPayoutFailed(@Param("id") String id, @Param("failReason") String failReason);
}