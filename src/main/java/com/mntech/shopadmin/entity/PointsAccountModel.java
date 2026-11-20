package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分账户
 */
@Data
@TableName("points_account")
public class PointsAccountModel {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户 ID */
    @TableField("user_id")
    private String userId;

    /** 总积分 */
    @TableField("total_points")
    private Integer totalPoints;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 用户昵称（关联查询） */
    @TableField(exist = false)
    private String userNickname;

    /** 今日是否已签到（关联查询，1=是 0=否） */
    @TableField(exist = false)
    private Integer todaySigned;
}