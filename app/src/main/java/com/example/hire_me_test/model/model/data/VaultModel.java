package com.example.hire_me_test.model.model.data;

public class VaultModel {
    public String jobId, updatedAt, transactionType, status;
    public double salary, otHours, otSalary, total;

    public VaultModel(String jobId, double salary, double otHours, double otSalary,
                      String updatedAt, String transactionType, String status) {
        this.jobId = jobId;
        this.salary = salary;  // Keep salary as it is from DB
        this.otHours = otHours;
        this.otSalary = otSalary;

        // ✅ total = salary + (ot_hours * ot_salary)
        this.total = salary + (otHours * otSalary);

        this.updatedAt = updatedAt;
        this.transactionType = transactionType;
        this.status = status;
    }
}
