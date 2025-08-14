package com.example.hire_me_test.model.model.data;

public class WithdrawRequestModel {
    private int id;
    private String idNumber;
    private int jobId;
    private double amount;
    private String status;
    private String requestedAt;

    public WithdrawRequestModel(int id, String idNumber, int jobId, double amount, String status, String requestedAt) {
        this.id = id;
        this.idNumber = idNumber;
        this.jobId = jobId;
        this.amount = amount;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public int getId() {
        return id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public int getJobId() {
        return jobId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
