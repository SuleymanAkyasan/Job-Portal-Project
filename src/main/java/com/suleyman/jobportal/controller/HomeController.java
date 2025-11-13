package com.suleyman.jobportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home(){
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to the Job Portal API");
        response.put("status", "OK");

        return response;
    }

}