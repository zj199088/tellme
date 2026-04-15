package com.fitness.app.dto;

public class CreatePlanRequest {
    private Long template_id;
    private String name;
    private String description;
    private int duration_weeks;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration_weeks() {
        return duration_weeks;
    }

    public void setDuration_weeks(int duration_weeks) {
        this.duration_weeks = duration_weeks;
    }
}
