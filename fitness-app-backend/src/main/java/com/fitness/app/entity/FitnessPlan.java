package com.fitness.app.entity;

import java.util.Date;

public class FitnessPlan {
    private Long id;
    private Long user_id;
    private Long template_id;
    private String name;
    private String type;
    private String goal;
    private String difficulty;
    private int duration_weeks;
    private String start_date;
    private String end_date;
    private String status;
    private boolean is_shared;
    private String shared_code;
    private String last_workout_date;
    private Date created_at;
    private Date updated_at;
    private boolean is_deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Long template_id) {
        this.template_id = template_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getDuration_weeks() {
        return duration_weeks;
    }

    public void setDuration_weeks(int duration_weeks) {
        this.duration_weeks = duration_weeks;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIs_shared() {
        return is_shared;
    }

    public void setIs_shared(boolean is_shared) {
        this.is_shared = is_shared;
    }

    public String getShared_code() {
        return shared_code;
    }

    public void setShared_code(String shared_code) {
        this.shared_code = shared_code;
    }

    public String getLast_workout_date() {
        return last_workout_date;
    }

    public void setLast_workout_date(String last_workout_date) {
        this.last_workout_date = last_workout_date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
