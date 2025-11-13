package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.ApplyJobRequestDto;
import com.suleyman.jobportal.dto.JobSeekerApplyResponseDto;
import com.suleyman.jobportal.entity.*;
import com.suleyman.jobportal.service.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable("jobId") int jobId,
                                         @RequestBody(required = false) ApplyJobRequestDto requestBody) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            JobSeekerProfile seekerProfile = jobSeekerProfileService.getOne(user.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job Seeker profile not found"));

            JobPostActivity jobPostActivity = jobPostActivityService.getOne(jobId);

            JobSeekerApply newApply = new JobSeekerApply();
            newApply.setUserId(seekerProfile);
            newApply.setJob(jobPostActivity);
            newApply.setApplyDate(new Date());

            if (requestBody != null && requestBody.getCoverLetter() != null) {
                newApply.setCoverLetter(requestBody.getCoverLetter());
            }

            JobSeekerApply savedApply = jobSeekerApplyService.addNew(newApply);
            JobSeekerApplyResponseDto responseDto = new JobSeekerApplyResponseDto(savedApply);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "You have already applied for this job"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/jobs/{jobId}/applicants")
    public ResponseEntity<?> getJobApplicants(@PathVariable("jobId") int jobId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden
                    .body(Map.of("error", "Only recruiters can view applicants."));
        }

        try {
            JobPostActivity jobDetails = jobPostActivityService.getOne(jobId);

            RecruiterProfile recruiter = recruiterProfileService.getCurrentRecruiterProfile();
            if (recruiter == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Recruiter profile not found"));
            }
            if (jobDetails.getPostedById().getUserId() != recruiter.getUserAccountId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden
                        .body(Map.of("error", "You do not have permission to view applicants for this job."));
            }

            List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);

            List<JobSeekerApplyResponseDto> responseDtoList = jobSeekerApplyList.stream()
                    .map(JobSeekerApplyResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDtoList);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

