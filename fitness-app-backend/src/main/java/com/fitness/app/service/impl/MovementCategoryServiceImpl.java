package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.MovementCategory;
import com.fitness.app.mapper.MovementCategoryMapper;
import com.fitness.app.service.MovementCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementCategoryServiceImpl extends ServiceImpl<MovementCategoryMapper, MovementCategory> implements MovementCategoryService {

    @Override
    public List<MovementCategory> getAllCategories() {
        return list();
    }
}
