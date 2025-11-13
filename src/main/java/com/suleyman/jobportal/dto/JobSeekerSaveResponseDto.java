package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobSeekerSave;

public class JobSeekerSaveResponseDto {

    private Integer id;
    private String userEmail;
    private String jobTitle;

    public JobSeekerSaveResponseDto(JobSeekerSave save) {
        this.id = save.getId();
        if (save.getUserId() != null && save.getUserId().getUserId() != null) {
            this.userEmail = save.getUserId().getUserId().getEmail();
        }
        if (save.getJob() != null) {
            this.jobTitle = save.getJob().getJobTitle();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}