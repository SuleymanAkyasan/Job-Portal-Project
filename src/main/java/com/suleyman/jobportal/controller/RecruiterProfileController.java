package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.RecruiterProfileDto;
import com.suleyman.jobportal.dto.RecruiterProfileResponseDto;
import com.suleyman.jobportal.entity.RecruiterProfile;
import com.suleyman.jobportal.entity.Users;
import com.suleyman.jobportal.repository.UsersRepository;
import com.suleyman.jobportal.service.RecruiterProfileService;
import com.suleyman.jobportal.util.FileUploadUtil;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/v1/profile/recruiter")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<RecruiterProfileResponseDto> getMyProfile() {
        RecruiterProfile recruiterProfile = recruiterProfileService.getCurrentRecruiterProfile();

        if (recruiterProfile == null) {
            return ResponseEntity.notFound().build();
        }

        RecruiterProfileResponseDto responseDto = new RecruiterProfileResponseDto(recruiterProfile);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/my-profile")
    public ResponseEntity<RecruiterProfileResponseDto> updateMyProfileText(@RequestBody RecruiterProfileDto profileDto) {

        RecruiterProfile profile = recruiterProfileService.getCurrentRecruiterProfile();

        if (profile == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("Couldn't found user"));
            profile = new RecruiterProfile(users);
        }

        if (profileDto.getFirstName() != null) profile.setFirstName(profileDto.getFirstName());
        if (profileDto.getLastName() != null) profile.setLastName(profileDto.getLastName());
        if (profileDto.getCity() != null) profile.setCity(profileDto.getCity());
        if (profileDto.getState() != null) profile.setState(profileDto.getState());
        if (profileDto.getCountry() != null) profile.setCountry(profileDto.getCountry());
        if (profileDto.getCompany() != null) profile.setCompany(profileDto.getCompany());

        RecruiterProfile savedProfile = recruiterProfileService.addNew(profile);

        RecruiterProfileResponseDto responseDto = new RecruiterProfileResponseDto(savedProfile);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/my-profile/photo")
    public ResponseEntity<RecruiterProfileResponseDto> updateMyProfilePhoto(@RequestParam("image") MultipartFile multipartFile) throws IOException {

        RecruiterProfile profile = recruiterProfileService.getCurrentRecruiterProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found. Please update profile details first.");
        }

        String fileName = "";
        if (multipartFile != null && !multipartFile.getOriginalFilename().equals("")){
            fileName= StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            profile.setProfilePhoto(fileName);
        } else {
            return ResponseEntity.badRequest().build();
        }

        String uploadDir = "photos/recruiter/" + profile.getUserAccountId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        RecruiterProfile savedProfile = recruiterProfileService.addNew(profile);

        RecruiterProfileResponseDto responseDto = new RecruiterProfileResponseDto(savedProfile);
        return ResponseEntity.ok(responseDto);
    }
}