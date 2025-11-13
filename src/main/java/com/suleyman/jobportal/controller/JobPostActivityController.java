package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.JobPostActivityResponseDto;
import com.suleyman.jobportal.dto.JobPostCreateDto;
import com.suleyman.jobportal.entity.*;
import com.suleyman.jobportal.service.JobPostActivityService;
import com.suleyman.jobportal.service.JobSeekerApplyService;
import com.suleyman.jobportal.service.JobSeekerSaveService;
import com.suleyman.jobportal.service.UsersService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/jobs/search")
    public ResponseEntity<List<JobPostActivityResponseDto>> globalSearch(
            @RequestParam(value = "job", required = false) String job,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "partTime", required = false) String partTime,
            @RequestParam(value = "fullTime", required = false) String fullTime,
            @RequestParam(value = "freelance", required = false) String freelance,
            @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
            @RequestParam(value = "officeOnly", required = false) String officeOnly,
            @RequestParam(value = "partialRemote", required = false) String partialRemote,
            @RequestParam(value = "today", required = false) boolean today,
            @RequestParam(value = "days7", required = false) boolean days7,
            @RequestParam(value = "days30", required = false) boolean days30) {

        LocalDate searchDate = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        if (days30) searchDate = LocalDate.now().minusDays(30);
        else if (days7) searchDate = LocalDate.now().minusDays(7);
        else if (today) searchDate = LocalDate.now();
        else dateSearchFlag = false;

        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time"; fullTime = "Full-Time"; freelance = "Freelance";
            remote = false;
        }
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only"; remoteOnly = "Remote-Only"; partialRemote = "Partial-Remote";
            type = false;
        }

        List<JobPostActivity> jobPost;
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }

        List<JobPostActivityResponseDto> responseDtoList = jobPost.stream()
                .map(JobPostActivityResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/dashboard/jobs")
    public ResponseEntity<?> getDashboardJobs(

    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Object currentUserProfile = usersService.getCurrentUserProfile();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {

            List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile)
                    currentUserProfile).getUserAccountId());

            return ResponseEntity.ok(recruiterJobs);

        } else {

            List<JobPostActivity> jobPost = jobPostActivityService.getAll();

            List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.
                    getCandidatesJobs((JobSeekerProfile) currentUserProfile);
            List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile)
                    currentUserProfile);

            for (JobPostActivity jobActivity : jobPost) {
                boolean exist = false;
                boolean saved = false;
                for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                    if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                        jobActivity.setIsActive(true);
                        exist = true;
                        break;
                    }
                }
                for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                    if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                        jobActivity.setIsSaved(true);
                        saved = true;
                        break;
                    }
                }
                if (!exist) jobActivity.setIsActive(false);
                if (!saved) jobActivity.setIsSaved(false);
            }

            List<JobPostActivityResponseDto> responseDtoList = jobPost.stream()
                    .map(JobPostActivityResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDtoList);
        }
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobPostActivityResponseDto> getJobById(@PathVariable("id") int id) {
        try {
            JobPostActivity job = jobPostActivityService.getOne(id);
            JobPostActivityResponseDto responseDto = new JobPostActivityResponseDto(job);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobPostActivityResponseDto> createNewJob(@RequestBody JobPostCreateDto jobDto) {

        Users user = usersService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        JobPostActivity newJob = new JobPostActivity();

        newJob.setJobTitle(jobDto.getJobTitle());
        newJob.setDescriptionOfJob(jobDto.getDescriptionOfJob());
        newJob.setJobType(jobDto.getJobType());
        newJob.setSalary(jobDto.getSalary());
        newJob.setRemote(jobDto.getRemote());
        newJob.setJobLocationId(jobDto.getJobLocation());
        newJob.setJobCompanyId(jobDto.getJobCompany());

        newJob.setPostedById(user);
        newJob.setPostedDate(new Date());
        newJob.setJobPostId(null);

        JobPostActivity savedJob = jobPostActivityService.addNew(newJob);

        JobPostActivityResponseDto responseDto = new JobPostActivityResponseDto(savedJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<JobPostActivityResponseDto> updateJob(@PathVariable("id") int id,
                                                                @RequestBody JobPostCreateDto jobDto) {

        Users user = usersService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        JobPostActivity existingJob;
        try {
            existingJob = jobPostActivityService.getOne(id);

            if (existingJob.getPostedById().getUserId() != user.getUserId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        if (jobDto.getJobTitle() != null) {
            existingJob.setJobTitle(jobDto.getJobTitle());
        }
        if (jobDto.getDescriptionOfJob() != null) {
            existingJob.setDescriptionOfJob(jobDto.getDescriptionOfJob());
        }
        if (jobDto.getJobType() != null) {
            existingJob.setJobType(jobDto.getJobType());
        }
        if (jobDto.getSalary() != null) {
            existingJob.setSalary(jobDto.getSalary());
        }
        if (jobDto.getRemote() != null) {
            existingJob.setRemote(jobDto.getRemote());
        }

        if (jobDto.getJobLocation() != null) {
            existingJob.setJobLocationId(jobDto.getJobLocation());
        }
        if (jobDto.getJobCompany() != null) {
            existingJob.setJobCompanyId(jobDto.getJobCompany());
        }

        JobPostActivity savedJob = jobPostActivityService.addNew(existingJob);

        JobPostActivityResponseDto responseDto = new JobPostActivityResponseDto(savedJob);
        return ResponseEntity.ok(responseDto);
    }
}