package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.entity.PointsRecordModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecordModel> {

    /**
     * 积分流水分页（关联用户昵称）
     */
    @Select("<script>" +
            "SELECT r.*, u.nickname AS user_nickname " +
            "FROM points_record r " +
            "LEFT JOIN users u ON u.id = r.user_id " +
            "WHERE r.deleted = 0 " +
            "<if test='userId != null and userId != \"\"'>AND r.user_id = #{userId}</if> " +
            "<if test='changeType != null and changeType != \"\" and changeType != \"ALL\"'>AND r.change_type = #{changeType}</if> " +
            "ORDER BY r.created_at DESC" +
            "</script>")
    IPage<PointsRecordModel> selectRecordPage(Page<?> page,
                                              @Param("userId") String userId,
                                              @Param("changeType") String changeType);
}