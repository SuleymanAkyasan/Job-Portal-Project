package com.suleyman.jobportal.service;

import com.suleyman.jobportal.entity.JobPostActivity;
import com.suleyman.jobportal.entity.JobSeekerProfile;
import com.suleyman.jobportal.entity.JobSeekerSave;
import com.suleyman.jobportal.repository.JobSeekerSaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId) {
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job) {
        return jobSeekerSaveRepository.findByJob(job);
    }

    public JobSeekerSave addNew(JobSeekerSave jobSeekerSave) {
        return jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
