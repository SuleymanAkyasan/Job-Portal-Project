package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.JobPostActivityResponseDto;
import com.suleyman.jobportal.dto.JobSeekerSaveResponseDto;
import com.suleyman.jobportal.entity.JobPostActivity;
import com.suleyman.jobportal.entity.JobSeekerProfile;
import com.suleyman.jobportal.entity.JobSeekerSave;
import com.suleyman.jobportal.entity.Users;
import com.suleyman.jobportal.service.JobPostActivityService;
import com.suleyman.jobportal.service.JobSeekerProfileService;
import com.suleyman.jobportal.service.JobSeekerSaveService;
import com.suleyman.jobportal.service.UsersService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("/jobs/{id}/save")
    public ResponseEntity<?> saveJob(@PathVariable("id") int id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            JobSeekerProfile seekerProfile = jobSeekerProfileService.getOne(user.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job Seeker profile not found"));

            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            JobSeekerSave jobSeekerSave = new JobSeekerSave();
            jobSeekerSave.setJob(jobPostActivity);
            jobSeekerSave.setUserId(seekerProfile);

            JobSeekerSave savedSave = jobSeekerSaveService.addNew(jobSeekerSave);

            JobSeekerSaveResponseDto responseDto = new JobSeekerSaveResponseDto(savedSave);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "You have already saved this job"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/seeker/saved-jobs")
    public ResponseEntity<List<JobPostActivityResponseDto>> getSavedJobs() {

        Object currentUserProfile = usersService.getCurrentUserProfile();
        if (currentUserProfile == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        List<JobPostActivityResponseDto> jobPostDtos = jobSeekerSaveList.stream()
                .map(jobSeekerSave -> new JobPostActivityResponseDto(jobSeekerSave.getJob()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobPostDtos);
    }
}