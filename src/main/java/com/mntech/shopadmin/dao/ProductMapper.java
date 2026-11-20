package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.ProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<ProductModel> {

    @Select("<script>" +
            "SELECT * FROM products WHERE 1=1" +
            "<if test='search != null and search != \"\"'>" +
            " AND (name ILIKE CONCAT('%', #{search}, '%') OR category ILIKE CONCAT('%', #{search}, '%'))" +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<ProductModel> searchProducts(@Param("search") String search,
                                      @Param("status") String status);
}