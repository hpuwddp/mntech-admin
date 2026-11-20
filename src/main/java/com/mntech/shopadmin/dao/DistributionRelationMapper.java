package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.DistributionRelationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DistributionRelationMapper extends BaseMapper<DistributionRelationModel> {

    /**
     * 分销商（已绑定上级）总数
     */
    @Select("SELECT COUNT(*) FROM distribution_relation WHERE deleted = 0")
    long countAll();
}