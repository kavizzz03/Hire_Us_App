package com.example.hire_me_test.model.model.data;

public class WorkerApplication {
    private int id;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String appliedAt;

    public WorkerApplication(int id, String jobId, String jobTitle, String companyName, String appliedAt){
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.appliedAt = appliedAt;
    }

    public int getId(){ return id; }
    public String getJobId(){ return jobId; }
    public String getJobTitle(){ return jobTitle; }
    public String getCompanyName(){ return companyName; }
    public String getAppliedAt(){ return appliedAt; }
}
