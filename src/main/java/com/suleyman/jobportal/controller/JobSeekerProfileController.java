package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.JobSeekerProfileDto;
import com.suleyman.jobportal.dto.JobSeekerProfileResponseDto;
import com.suleyman.jobportal.entity.JobSeekerProfile;
import com.suleyman.jobportal.entity.Skills;
import com.suleyman.jobportal.entity.Users;
import com.suleyman.jobportal.repository.UsersRepository;
import com.suleyman.jobportal.service.JobSeekerProfileService;
import com.suleyman.jobportal.util.FileDownloadUtil;
import com.suleyman.jobportal.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/profile/seeker")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;
    private final UsersRepository usersRepository;

    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<JobSeekerProfileResponseDto> getMyProfile() {
        JobSeekerProfile profile = jobSeekerProfileService.getCurrentSeekerProfile();

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        JobSeekerProfileResponseDto dto = new JobSeekerProfileResponseDto(profile);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSeekerProfileResponseDto> getCandidateProfileById(@PathVariable("id") int id) {
        JobSeekerProfile seekerProfile = jobSeekerProfileService.getOne(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        JobSeekerProfileResponseDto dto = new JobSeekerProfileResponseDto(seekerProfile);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/my-profile")
    public ResponseEntity<JobSeekerProfileResponseDto> updateMyProfileText(@RequestBody JobSeekerProfileDto profileDto) {

        JobSeekerProfile profile = jobSeekerProfileService.getCurrentSeekerProfile();

        if (profile == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users user = usersRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));
            profile = new JobSeekerProfile(user);
        }

        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setCity(profileDto.getCity());
        profile.setState(profileDto.getState());
        profile.setCountry(profileDto.getCountry());
        profile.setWorkAuthorization(profileDto.getWorkAuthorization());
        profile.setEmploymentType(profileDto.getEmploymentType());

        if (profileDto.getSkills() != null) {
            if (profile.getSkills() == null) {
                profile.setSkills(new ArrayList<>());
            }

            profile.getSkills().clear();

            for (String skillName : profileDto.getSkills()) {
                Skills skill = new Skills();
                skill.setName(skillName);
                skill.setJobSeekerProfile(profile);
                profile.getSkills().add(skill);
            }
        }

        JobSeekerProfile savedProfile = jobSeekerProfileService.addNew(profile);
        JobSeekerProfileResponseDto responseDto = new JobSeekerProfileResponseDto(savedProfile);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/my-profile/photo")
    public ResponseEntity<JobSeekerProfileResponseDto> updateMyProfilePhoto(@RequestParam("image") MultipartFile image) throws IOException {

        JobSeekerProfile profile = jobSeekerProfileService.getCurrentSeekerProfile();
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String imageName = "";
        if (image != null && !Objects.equals(image.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            profile.setProfilePhoto(imageName);
        } else {
            return ResponseEntity.badRequest().build();
        }

        String uploadDir = "photos/candidate/" + profile.getUserAccountId();
        FileUploadUtil.saveFile(uploadDir, imageName, image);

        JobSeekerProfile savedProfile = jobSeekerProfileService.addNew(profile);
        JobSeekerProfileResponseDto responseDto = new JobSeekerProfileResponseDto(savedProfile);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/my-profile/resume")
    public ResponseEntity<JobSeekerProfileResponseDto> updateMyProfileResume(@RequestParam("resume") MultipartFile pdf) throws IOException {

        JobSeekerProfile profile = jobSeekerProfileService.getCurrentSeekerProfile();
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String resumeName = "";
        if (pdf != null && !Objects.equals(pdf.getOriginalFilename(), "")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            profile.setResume(resumeName);
        } else {
            return ResponseEntity.badRequest().build();
        }

        String uploadDir = "photos/candidate/" + profile.getUserAccountId();
        FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
        JobSeekerProfile savedProfile = jobSeekerProfileService.addNew(profile);
        JobSeekerProfileResponseDto responseDto = new JobSeekerProfileResponseDto(savedProfile);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/download-resume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName,
                                            @RequestParam(value = "userID") String userId) {

        Resource resource;
        try {
            resource = FileDownloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);

        } catch (IOException e) {
            return new ResponseEntity<>("File not found or unreadable: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}