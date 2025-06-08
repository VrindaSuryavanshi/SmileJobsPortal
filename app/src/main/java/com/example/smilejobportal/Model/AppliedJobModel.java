
package com.example.smilejobportal.Model;

public class AppliedJobModel {

    public String title;
    public String company;
    public String location;
    public String salary;

    public String jobType;
    public String time;
    public String model;
    public String dateApplied;
    public String status;
    public String resumeUrl;

    public AppliedJobModel() {
        // Required for Firebase
    }

    public AppliedJobModel(String title, String company, String location, String salary, String jobType,
                           String time, String model, String dateApplied, String status, String resumeUrl) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.time = time;
        this.model = model;
        this.dateApplied = dateApplied;
        this.status = status;
        this.resumeUrl = resumeUrl;
    }

    // Getters and setters (optional, useful if you want to use encapsulation)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(String dateApplied) {
        this.dateApplied = dateApplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}
