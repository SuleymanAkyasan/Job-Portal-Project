package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.RecruiterProfile;

public class RecruiterProfileResponseDto {

    private int userAccountId;
    private UserResponseDto user;
    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String company;
    private String profilePhoto;
    private String photosImagePath;

    public RecruiterProfileResponseDto(RecruiterProfile profile) {
        this.userAccountId = profile.getUserAccountId();

        if (profile.getUserId() != null) {
            this.user = new UserResponseDto(profile.getUserId());
        }

        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.city = profile.getCity();
        this.state = profile.getState();
        this.country = profile.getCountry();
        this.company = profile.getCompany();
        this.profilePhoto = profile.getProfilePhoto();
        this.photosImagePath = profile.getPhotosImagePath();
    }

    public int getUserAccountId() { return userAccountId; }
    public void setUserAccountId(int userAccountId) { this.userAccountId = userAccountId; }
    public UserResponseDto getUser() { return user; }
    public void setUser(UserResponseDto user) { this.user = user; }
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
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public String getPhotosImagePath() { return photosImagePath; }
    public void setPhotosImagePath(String photosImagePath) { this.photosImagePath = photosImagePath; }
}