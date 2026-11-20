package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.PointsAccountModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointsAccountMapper extends BaseMapper<PointsAccountModel> {

    /**
     * 积分账户分页（关联用户昵称/今日签到状态）
     */
    @Select("<script>" +
            "SELECT a.*, u.nickname AS user_nickname, " +
            "(SELECT COUNT(*) FROM points_sign s WHERE s.user_id = a.user_id AND s.deleted = 0 AND s.sign_date = CURRENT_DATE) AS today_signed " +
            "FROM points_account a " +
            "LEFT JOIN users u ON u.id = a.user_id " +
            "WHERE a.deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>AND (u.nickname LIKE '%' || #{keyword} || '%' OR a.user_id = #{keyword})</if> " +
            "ORDER BY a.total_points DESC, a.created_at DESC" +
            "</script>")
    IPage<PointsAccountModel> selectAccountPage(Page<?> page, @Param("keyword") String keyword);
}