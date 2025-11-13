package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.JobCompany;
import com.suleyman.jobportal.entity.JobLocation;

public class JobPostCreateDto {

    private String jobTitle;
    private String descriptionOfJob;
    private String jobType;
    private String salary;
    private String remote;

    private JobLocation jobLocation;
    private JobCompany jobCompany;

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getDescriptionOfJob() { return descriptionOfJob; }
    public void setDescriptionOfJob(String descriptionOfJob) { this.descriptionOfJob = descriptionOfJob; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getRemote() { return remote; }
    public void setRemote(String remote) { this.remote = remote; }
    public JobLocation getJobLocation() { return jobLocation; }
    public void setJobLocation(JobLocation jobLocation) { this.jobLocation = jobLocation; }
    public JobCompany getJobCompany() { return jobCompany; }
    public void setJobCompany(JobCompany jobCompany) { this.jobCompany = jobCompany; }
}