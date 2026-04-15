package com.fitness.app.mapper;

import com.fitness.app.entity.FitnessPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface FitnessPlanMapper {

    @Insert("INSERT INTO fitness_plans (user_id, template_id, name, type, goal, difficulty, duration_weeks, start_date, status, is_deleted, created_at, updated_at) VALUES (#{user_id}, #{template_id}, #{name}, #{type}, #{goal}, #{difficulty}, #{duration_weeks}, #{start_date}, #{status}, #{is_deleted}, #{created_at}, #{updated_at})")
    void insert(FitnessPlan plan);
}
