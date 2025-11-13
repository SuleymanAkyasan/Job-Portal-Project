package com.suleyman.jobportal.dto;

import java.util.List;

public class JobSeekerProfileDto {

    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String workAuthorization;
    private String employmentType;
    private List<String> skills;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getWorkAuthorization() { return workAuthorization; }
    public void setWorkAuthorization(String workAuthorization) { this.workAuthorization = workAuthorization; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}