package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日签到记录
 */
@Data
@TableName("points_sign")
public class PointsSignModel {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户 ID */
    @TableField("user_id")
    private String userId;

    /** 签到日期 */
    @TableField("sign_date")
    private LocalDate signDate;

    /** 本次签到积分 */
    @TableField("points")
    private Integer points;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}