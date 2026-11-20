package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE user_name = #{username}")
    SysUser findByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser findByEmail(String email);
}