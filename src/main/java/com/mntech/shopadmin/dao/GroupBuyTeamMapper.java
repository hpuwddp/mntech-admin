package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.GroupBuyTeamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GroupBuyTeamMapper extends BaseMapper<GroupBuyTeamModel> {

    /**
     * 分页查询团记录（关联商品/团长/活动信息）
     */
    @Select("<script>" +
            "SELECT t.*, p.name AS product_name, p.main_image AS product_image, " +
            "u.nickname AS leader_nickname, u.avatar AS leader_avatar, a.name AS activity_name " +
            "FROM group_buy_team t " +
            "LEFT JOIN group_buy_product gbp ON gbp.id = t.group_buy_product_id " +
            "LEFT JOIN product p ON p.id = gbp.product_id " +
            "LEFT JOIN users u ON u.id = t.leader_user_id " +
            "LEFT JOIN group_buy_activity a ON a.id = t.activity_id " +
            "WHERE t.deleted = 0 " +
            "<if test='groupNo != null and groupNo != \"\"'>AND t.group_no LIKE CONCAT('%', #{groupNo}, '%')</if> " +
            "<if test='activityId != null and activityId != \"\"'>AND t.activity_id = #{activityId}</if> " +
            "<if test='status != null and status != \"\" and status != \"ALL\"'>AND t.status = #{status}</if> " +
            "ORDER BY t.created_at DESC" +
            "</script>")
    IPage<GroupBuyTeamModel> selectPageWithJoin(Page<?> page,
                                                @Param("groupNo") String groupNo,
                                                @Param("activityId") String activityId,
                                                @Param("status") String status);
}