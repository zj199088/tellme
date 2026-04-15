package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.MovementCategory;

import java.util.List;

public interface MovementCategoryService extends IService<MovementCategory> {
    List<MovementCategory> getAllCategories();
}