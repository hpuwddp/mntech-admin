package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.SeckillActivityModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivityModel> {

    /**
     * 获取进行中的活动
     */
    @Select("SELECT * FROM seckill_activity " +
            "WHERE status = 'APPROVED' " +
            "AND start_time <= NOW() " +
            "AND end_time >= NOW() " +
            "AND deleted = 0 " +
            "ORDER BY start_time ASC")
    List<SeckillActivityModel> selectOngoingActivities();

    /**
     * 获取即将开始的活动
     */
@Select("SELECT * FROM seckill_activity " +
            "WHERE status = 'APPROVED' " +
            "AND start_time > NOW() " +
            "AND start_time <= NOW() + INTERVAL '1 day' " +
            "AND deleted = 0 " +
            "ORDER BY start_time ASC")
    List<SeckillActivityModel> selectUpcomingActivities();

    /**
     * 更新活动状态
     */
    @Update("UPDATE seckill_activity SET status = #{status}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status);

    /**
     * 自动更新活动状态（定时任务）
     */
    @Update("UPDATE seckill_activity SET status = 'ENDED' " +
            "WHERE status = 'ONGOING' AND end_time < NOW()")
    int autoEndActivities();

    @Update("UPDATE seckill_activity SET status = 'ONGOING' " +
            "WHERE status = 'APPROVED' AND start_time <= NOW() AND end_time >= NOW()")
    int autoStartActivities();

    /**
     * 统计活动商品数
     */
    @Select("SELECT COUNT(*) FROM seckill_product WHERE activity_id = #{activityId} AND deleted = 0")
    int countProducts(@Param("activityId") String activityId);
}