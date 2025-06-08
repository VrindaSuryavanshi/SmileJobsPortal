package com.example.smilejobportal.Model;
public class CandidateByHrModel  {
    public String id, name, email, contact ,companyName,positionName,resumeFileName,resumeUrl;
     public String userId;
    ;

    public CandidateByHrModel() {
        // Default constructor required for Firebase
    }
    public CandidateByHrModel(String id, String name, String email, String contact, String companyName , String positionName, String resumeFileName,String resumeUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.companyName=companyName;
        this.positionName = positionName;
        this.resumeFileName=resumeFileName;
        this.resumeUrl=resumeUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public String getCompanyName() { return companyName; }
    public String getPositionName() { return positionName; }
    public String getResumeFileName() { return resumeFileName; }
    public String getResumeUrl() { return resumeUrl; }
    public String getUserId() { return userId; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setContact(String contact) { this.contact = contact; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public void setUserId(String userId) { this.userId = userId; }
}

