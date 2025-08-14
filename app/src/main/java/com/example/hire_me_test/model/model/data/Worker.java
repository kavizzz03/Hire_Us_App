package com.example.hire_me_test.model.model.data;

public class Worker {
    private String idNumber;
    private String fullName;
    private String email;
    private String contactNumber;
    private String workExperience;
    private String rating;
    private String feedback;
    private String status;

    public Worker(String idNumber, String fullName, String email,String status,String contactNumber, String workExperience, String rating, String feedback) {
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.contactNumber = contactNumber;
        this.workExperience = workExperience;
        this.rating = rating;
        this.feedback = feedback;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getIdNumber() { return idNumber; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getWorkExperience() { return workExperience; }
    public String getRating() { return rating; }
    public String getFeedback() { return feedback; }
}
