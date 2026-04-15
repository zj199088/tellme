package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.MovementCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovementCategoryMapper extends BaseMapper<MovementCategory> {
}