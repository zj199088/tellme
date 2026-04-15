package com.fitness.app.entity;

import java.util.Date;

public class TemplateDay {
    private Long id;
    private Long template_id;
    private int day_of_week;
    private boolean is_rest_day;
    private String rest_note;
    private int estimated_duration;
    private Date created_at;
    private Date updated_at;
    private boolean is_deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Long template_id) {
        this.template_id = template_id;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public boolean isIs_rest_day() {
        return is_rest_day;
    }

    public void setIs_rest_day(boolean is_rest_day) {
        this.is_rest_day = is_rest_day;
    }

    public String getRest_note() {
        return rest_note;
    }

    public void setRest_note(String rest_note) {
        this.rest_note = rest_note;
    }

    public int getEstimated_duration() {
        return estimated_duration;
    }

    public void setEstimated_duration(int estimated_duration) {
        this.estimated_duration = estimated_duration;
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
