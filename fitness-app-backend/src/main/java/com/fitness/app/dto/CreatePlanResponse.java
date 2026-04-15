package com.fitness.app.dto;

public class CreatePlanResponse {
    private Long plan_id;
    private boolean success;

    public CreatePlanResponse(Long plan_id, boolean success) {
        this.plan_id = plan_id;
        this.success = success;
    }

    public Long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Long plan_id) {
        this.plan_id = plan_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
