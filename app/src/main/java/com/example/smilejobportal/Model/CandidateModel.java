package com.example.smilejobportal.Model;

public class CandidateModel  {
    public String id, fullName, email, contact ,companyName,positionName;

    public CandidateModel() {
        // Default constructor required for Firebase
    }

    public CandidateModel(String id, String fullName, String email, String contact,String companyName ,String positionName) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.contact = contact;
        this.companyName=companyName;
        this.positionName = positionName;
    }
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public String getCompanyName() { return companyName; }
    public String getPositionName() { return positionName; }

    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setContact(String contact) { this.contact = contact; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

}

