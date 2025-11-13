package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobCompany;
import com.suleyman.jobportal.entity.JobLocation;
import com.suleyman.jobportal.entity.JobPostActivity;
import java.util.Date;

public class JobPostActivityResponseDto {

    private Integer jobPostId;
    private UserResponseDto postedBy;
    private JobLocation jobLocation;
    private JobCompany jobCompany;
    private String descriptionOfJob;
    private String jobType;
    private String salary;
    private String remote;
    private Date postedDate;
    private String jobTitle;

    private Boolean isActive;
    private Boolean isSaved;

    public JobPostActivityResponseDto(JobPostActivity job) {
        this.jobPostId = job.getJobPostId();

        if (job.getPostedById() != null) {
            this.postedBy = new UserResponseDto(job.getPostedById());
        }

        this.jobLocation = job.getJobLocationId();
        this.jobCompany = job.getJobCompanyId();
        this.descriptionOfJob = job.getDescriptionOfJob();
        this.jobType = job.getJobType();
        this.salary = job.getSalary();
        this.remote = job.getRemote();
        this.postedDate = job.getPostedDate();
        this.jobTitle = job.getJobTitle();
        this.isActive = job.getIsActive();
        this.isSaved = job.getIsSaved();
    }

    public Integer getJobPostId() { return jobPostId; }
    public void setJobPostId(Integer jobPostId) { this.jobPostId = jobPostId; }
    public UserResponseDto getPostedBy() { return postedBy; }
    public void setPostedBy(UserResponseDto postedBy) { this.postedBy = postedBy; }
    public JobLocation getJobLocation() { return jobLocation; }
    public void setJobLocation(JobLocation jobLocation) { this.jobLocation = jobLocation; }
    public JobCompany getJobCompany() { return jobCompany; }
    public void setJobCompany(JobCompany jobCompany) { this.jobCompany = jobCompany; }
    public String getDescriptionOfJob() { return descriptionOfJob; }
    public void setDescriptionOfJob(String descriptionOfJob) { this.descriptionOfJob = descriptionOfJob; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getRemote() { return remote; }
    public void setRemote(String remote) { this.remote = remote; }
    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public Boolean getIsSaved() { return isSaved; }
    public void setIsSaved(Boolean saved) { isSaved = saved; }
}