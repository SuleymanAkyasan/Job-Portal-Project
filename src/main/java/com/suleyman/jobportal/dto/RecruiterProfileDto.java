package com.suleyman.jobportal.dto;

public class RecruiterProfileDto {

    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String company;

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

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}