package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobSeekerProfile;
import com.suleyman.jobportal.entity.Skills;

import java.util.List;
import java.util.stream.Collectors;

public class JobSeekerProfileResponseDto {

    private Integer userAccountId;
    private UserResponseDto user;
    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    private String workAuthorization;
    private String employmentType;
    private String resume;
    private String profilePhoto;
    private String photosImagePath;
    private List<String> skills;

    public JobSeekerProfileResponseDto(JobSeekerProfile profile) {
        this.userAccountId = profile.getUserAccountId();
        this.user = new UserResponseDto(profile.getUserId());
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.city = profile.getCity();
        this.state = profile.getState();
        this.country = profile.getCountry();
        this.workAuthorization = profile.getWorkAuthorization();
        this.employmentType = profile.getEmploymentType();
        this.resume = profile.getResume();
        this.profilePhoto = profile.getProfilePhoto();
        this.photosImagePath = profile.getPhotosImagePath();

        if (profile.getSkills() != null) {
            this.skills = profile.getSkills().stream()
                    .map(Skills::getName)
                    .collect(Collectors.toList());
        }
    }

    public Integer getUserAccountId() { return userAccountId; }
    public void setUserAccountId(Integer userAccountId) { this.userAccountId = userAccountId; }
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
    public String getWorkAuthorization() { return workAuthorization; }
    public void setWorkAuthorization(String workAuthorization) { this.workAuthorization = workAuthorization; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public String getPhotosImagePath() { return photosImagePath; }
    public void setPhotosImagePath(String photosImagePath) { this.photosImagePath = photosImagePath; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}