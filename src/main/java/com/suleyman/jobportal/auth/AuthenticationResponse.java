package com.suleyman.jobportal.auth; // veya .dto

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
}