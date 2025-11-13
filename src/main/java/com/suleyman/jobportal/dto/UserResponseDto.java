package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.Users;
import java.util.Date;

public class UserResponseDto {

    private int userId;
    private String email;
    private Date registrationDate;
    private String userType;

    public UserResponseDto(Users user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.registrationDate = user.getRegistrationDate();
        this.userType = user.getUserTypeId().getUserTypeName();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}