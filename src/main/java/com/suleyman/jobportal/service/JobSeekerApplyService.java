package com.suleyman.jobportal.service;

import com.suleyman.jobportal.entity.JobPostActivity;
import com.suleyman.jobportal.entity.JobSeekerApply;
import com.suleyman.jobportal.entity.JobSeekerProfile;
import com.suleyman.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;


    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId){

        return jobSeekerApplyRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job){

        return jobSeekerApplyRepository.findByJob(job);
    }

    public JobSeekerApply addNew(JobSeekerApply jobSeekerApply) {
        return jobSeekerApplyRepository.save(jobSeekerApply);
    }
}
