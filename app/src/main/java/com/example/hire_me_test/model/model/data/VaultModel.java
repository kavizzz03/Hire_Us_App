package com.example.hire_me_test.model.model.data;

public class VaultModel {
    public String jobId, updatedAt;
    public double salary, otHours, otSalary, total;

    public VaultModel(String jobId, double salary, double otHours, double otSalary, String updatedAt) {
        this.jobId = jobId;
        this.salary = salary;
        this.otHours = otHours;
        this.otSalary = otSalary;
        this.total = salary + (otSalary * otHours);
        this.updatedAt = updatedAt;
    }
}
