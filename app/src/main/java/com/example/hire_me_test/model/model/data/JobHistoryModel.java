package com.example.hire_me_test.model.model.data;

public class JobHistoryModel {
    private String jobId;
    private String hiredAt;
    private String wantsMeals;

    public JobHistoryModel(String jobId, String hiredAt, String wantsMeals) {
        this.jobId = jobId;
        this.hiredAt = hiredAt;
        this.wantsMeals = wantsMeals;
    }

    // Getters
    public String getJobId() {
        return jobId;
    }

    public String getHiredAt() {
        return hiredAt;
    }

    public String getWantsMeals() {
        return wantsMeals;
    }
}