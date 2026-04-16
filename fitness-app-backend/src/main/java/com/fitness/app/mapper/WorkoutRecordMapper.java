package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutRecord;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRecordMapper extends BaseMapper<WorkoutRecord> {
    @Select("SELECT * FROM workout_records WHERE user_id = #{userId} AND date = #{date}")
    WorkoutRecord getByUserIdAndDate(Integer userId, LocalDate date);
    
    @Select("SELECT wr.*, fp.name as plan_name, we.name as exercise_name " +
            "FROM workout_records wr " +
            "LEFT JOIN fitness_plans fp ON wr.plan_id = fp.id " +
            "LEFT JOIN workout_schedule_exercises we ON wr.schedule_exercise_id = we.id " +
            "WHERE wr.user_id = #{userId} " +
            "ORDER BY wr.date DESC LIMIT #{limit}")
    List<WorkoutRecord> getRecentRecords(Integer userId, int limit);
    
    @Select("SELECT wr.*, fp.name as plan_name, we.name as exercise_name " +
            "FROM workout_records wr " +
            "LEFT JOIN fitness_plans fp ON wr.plan_id = fp.id " +
            "LEFT JOIN workout_schedule_exercises we ON wr.schedule_exercise_id = we.id " +
            "WHERE wr.user_id = #{userId} " +
            "AND (wr.plan_id = #{planId} OR #{planId} IS NULL) " +
            "AND (wr.date = #{date} OR #{date} IS NULL) " +
            "ORDER BY wr.date DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<WorkoutRecord> getRecordsWithDetails(Integer userId, Integer planId, LocalDate date, int pageSize, int offset);
    
    @Select("SELECT COUNT(*) " +
            "FROM workout_records wr " +
            "WHERE wr.user_id = #{userId} " +
            "AND (wr.plan_id = #{planId} OR #{planId} IS NULL) " +
            "AND (wr.date = #{date} OR #{date} IS NULL)")
    int countRecords(Integer userId, Integer planId, LocalDate date);
    
    @Select("SELECT COUNT(*) FROM workout_records WHERE user_id = #{userId}")
    int countAllRecords(Integer userId);
}