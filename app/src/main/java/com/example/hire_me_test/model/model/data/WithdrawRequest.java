package com.example.hire_me_test.model.model.data;
public class WithdrawRequest {
    private int id, jobId;
    private String idNumber, status, requestedAt;
    private double amount;

    public WithdrawRequest(int id, int jobId, String idNumber, double amount, String status, String requestedAt){
        this.id = id;
        this.jobId = jobId;
        this.idNumber = idNumber;
        this.amount = amount;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public int getId() { return id; }
    public int getJobId() { return jobId; }
    public String getIdNumber() { return idNumber; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getRequestedAt() { return requestedAt; }
    public void setStatus(String status) { this.status = status; }
}
