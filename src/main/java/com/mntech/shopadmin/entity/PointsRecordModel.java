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
 * 积分流水
 */
@Data
@TableName("points_record")
public class PointsRecordModel {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户 ID */
    @TableField("user_id")
    private String userId;

    /** 变动类型：SIGN 每日签到 / SIGN_BONUS 连续签到奖励 */
    @TableField("change_type")
    private String changeType;

    /** 变动积分（正为增加） */
    @TableField("points")
    private Integer points;

    /** 备注 */
    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /** 用户昵称（关联查询） */
    @TableField(exist = false)
    private String userNickname;
}