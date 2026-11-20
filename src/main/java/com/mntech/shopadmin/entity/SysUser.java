package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@TableName("sys_user")
public class SysUser implements UserDetails {
    @TableId(type = IdType.AUTO)  // 使用数据库自增
    private Long id;

    @TableField("user_name")
    private String userName;

    @TableField("pass_word")
    private String passWord;

    @TableField("email")
    private String email;
    @TableField("status")
    private Integer status;
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField("role")
    private String role;

// ========== UserDetails 接口方法 ==========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return this.passWord;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }
}
