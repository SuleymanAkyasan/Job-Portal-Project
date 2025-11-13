package com.suleyman.jobportal.controller;

import com.suleyman.jobportal.dto.RegisterRequestDto;
import com.suleyman.jobportal.dto.UserResponseDto;
import com.suleyman.jobportal.dto.UserTypeResponseDto;
import com.suleyman.jobportal.entity.Users;
import com.suleyman.jobportal.entity.UsersType;
import com.suleyman.jobportal.repository.UsersTypeRepository;
import com.suleyman.jobportal.service.UsersService;
import com.suleyman.jobportal.service.UsersTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;
    private final UsersTypeRepository usersTypeRepository;

    public UsersController(UsersTypeService usersTypeService, UsersService usersService,
                           UsersTypeRepository usersTypeRepository) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
        this.usersTypeRepository = usersTypeRepository;
    }

    @GetMapping("/user-types")
    public ResponseEntity<List<UserTypeResponseDto>> getAllUserTypes() {

        List<UsersType> usersTypes = usersTypeService.getAll();

        List<UserTypeResponseDto> responseDtoList = usersTypes.stream()
                .map(UserTypeResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtoList);
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody RegisterRequestDto registerDto) {

        if (usersService.getUserByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        Users newUser = new Users();
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(registerDto.getPassword());

        UsersType userType = usersTypeRepository.findById(registerDto.getUserTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userTypeId"));

        newUser.setUserTypeId(userType);

        Users savedUser = usersService.addNewUser(newUser);
        UserResponseDto responseDto = new UserResponseDto(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}