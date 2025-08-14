package com.example.hire_me_test.model.model.data;

public class Review {
    private String ratedBy, workExperience, feedback, jobTitle, companyName, duration, createdAt;
    private int rating;

    public Review(String ratedBy, int rating, String workExperience, String feedback,
                  String jobTitle, String companyName, String duration, String createdAt) {
        this.ratedBy = ratedBy;
        this.rating = rating;
        this.workExperience = workExperience;
        this.feedback = feedback;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.duration = duration;
        this.createdAt = createdAt;
    }

    // Getters
    public String getRatedBy() { return ratedBy; }
    public int getRating() { return rating; }
    public String getWorkExperience() { return workExperience; }
    public String getFeedback() { return feedback; }
    public String getJobTitle() { return jobTitle; }
    public String getCompanyName() { return companyName; }
    public String getDuration() { return duration; }
    public String getCreatedAt() { return createdAt; }
}
