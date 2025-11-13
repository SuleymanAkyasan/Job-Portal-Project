package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobSeekerApply;
import java.util.Date;

public class JobSeekerApplyResponseDto {

    private Integer id;
    private Date applyDate;
    private String coverLetter;
    private String userEmail;
    private String jobTitle;

    public JobSeekerApplyResponseDto(JobSeekerApply apply) {
        this.id = apply.getId();
        this.applyDate = apply.getApplyDate();
        this.coverLetter = apply.getCoverLetter();

        if (apply.getUserId() != null && apply.getUserId().getUserId() != null) {
            this.userEmail = apply.getUserId().getUserId().getEmail();
        }
        if (apply.getJob() != null) {
            this.jobTitle = apply.getJob().getJobTitle();
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getApplyDate() { return applyDate; }
    public void setApplyDate(Date applyDate) { this.applyDate = applyDate; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}