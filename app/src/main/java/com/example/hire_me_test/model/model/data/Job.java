package com.example.hire_me_test.model.model.data;

public class Job {
    private int id;
    private String jobTitle;
    private String companyName;
    private String vacancies;
    private String timeRange;
    private String location;
    private String basicSalary;
    private String otSalary;
    private String requirements;
    private String jobDate;
    private String pickupLocation;
    private String contactInfo;
    private String email;

    public Job(int id, String jobTitle, String companyName, String vacancies,
               String timeRange, String location, String basicSalary, String otSalary,
               String requirements, String jobDate, String pickupLocation,
               String contactInfo, String email) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.vacancies = vacancies;
        this.timeRange = timeRange;
        this.location = location;
        this.basicSalary = basicSalary;
        this.otSalary = otSalary;
        this.requirements = requirements;
        this.jobDate = jobDate;
        this.pickupLocation = pickupLocation;
        this.contactInfo = contactInfo;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getVacancies() {
        return vacancies;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getLocation() {
        return location;
    }

    public String getBasicSalary() {
        return basicSalary;
    }

    public String getOtSalary() {
        return otSalary;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getJobDate() {
        return jobDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getEmail() {
        return email;
    }
}