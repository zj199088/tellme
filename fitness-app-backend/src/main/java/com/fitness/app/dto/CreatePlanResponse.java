package com.fitness.app.dto;

public class CreatePlanResponse {
    private Long planId;
    private boolean success;

    public CreatePlanResponse(Long planId, boolean success) {
        this.planId = planId;
        this.success = success;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
