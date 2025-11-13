package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobCompany;
import com.suleyman.jobportal.entity.JobLocation;

public class RecruiterJobsDto {

    private Long totalCandidates;
    private Integer jobPostId;
    private String jobTitle;
    private JobLocation jobLocation;
    private JobCompany jobCompany;

    public RecruiterJobsDto() {
    }

    public RecruiterJobsDto(Long totalCandidates, Integer jobPostId, String jobTitle, JobLocation jobLocation, JobCompany jobCompany) {
        this.totalCandidates = totalCandidates;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobCompany = jobCompany;
    }

    public Long getTotalCandidates() {
        return totalCandidates;
    }

    public void setTotalCandidates(Long totalCandidates) {
        this.totalCandidates = totalCandidates;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public JobLocation getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(JobLocation jobLocation) {
        this.jobLocation = jobLocation;
    }

    public JobCompany getJobCompany() {
        return jobCompany;
    }

    public void setJobCompany(JobCompany jobCompany) {
        this.jobCompany = jobCompany;
    }
}