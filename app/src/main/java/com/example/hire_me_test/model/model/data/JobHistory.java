package com.example.hire_me_test.model.model.data;

public class JobHistory {
    private int jobId;
    private String jobTitle;
    private String date;
    private String location;

    public JobHistory(int jobId, String jobTitle, String date, String location) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.date = date;
        this.location = location;
    }

    public int getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}
