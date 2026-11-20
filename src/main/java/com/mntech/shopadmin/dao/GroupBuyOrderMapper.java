package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.GroupBuyOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GroupBuyOrderMapper extends BaseMapper<GroupBuyOrderModel> {

    /**
     * 分页查询团购订单（关联商品/买家/团/活动信息）
     */
    @Select("<script>" +
            "SELECT o.*, p.name AS product_name, p.main_image AS product_image, " +
            "u.nickname AS buyer_nickname, u.avatar AS buyer_avatar, " +
            "t.group_no, a.name AS activity_name, t.status AS team_status " +
            "FROM group_buy_order o " +
            "LEFT JOIN group_buy_product gbp ON gbp.id = o.group_buy_product_id " +
            "LEFT JOIN product p ON p.id = gbp.product_id " +
            "LEFT JOIN users u ON u.id = o.user_id " +
            "LEFT JOIN group_buy_team t ON t.id = o.team_id " +
            "LEFT JOIN group_buy_activity a ON a.id = o.activity_id " +
            "WHERE o.deleted = 0 " +
            "<if test='orderNo != null and orderNo != \"\"'>AND o.order_no LIKE CONCAT('%', #{orderNo}, '%')</if> " +
            "<if test='userId != null and userId != \"\"'>AND o.user_id = #{userId}</if> " +
            "<if test='status != null and status != \"\" and status != \"ALL\"'>AND o.status = #{status}</if> " +
            "ORDER BY o.created_at DESC" +
            "</script>")
    IPage<GroupBuyOrderModel> selectPageWithJoin(Page<?> page,
                                                 @Param("orderNo") String orderNo,
                                                 @Param("userId") String userId,
                                                 @Param("status") String status);

    /**
     * 订单详情（关联商品/买家/团信息）
     */
    @Select("SELECT o.*, p.name AS product_name, p.main_image AS product_image, " +
            "u.nickname AS buyer_nickname, u.avatar AS buyer_avatar, " +
            "t.group_no, a.name AS activity_name, t.status AS team_status " +
            "FROM group_buy_order o " +
            "LEFT JOIN group_buy_product gbp ON gbp.id = o.group_buy_product_id " +
            "LEFT JOIN product p ON p.id = gbp.product_id " +
            "LEFT JOIN users u ON u.id = o.user_id " +
            "LEFT JOIN group_buy_team t ON t.id = o.team_id " +
            "LEFT JOIN group_buy_activity a ON a.id = o.activity_id " +
            "WHERE o.id = #{id} AND o.deleted = 0")
    GroupBuyOrderModel selectByIdWithJoin(@Param("id") String id);
}