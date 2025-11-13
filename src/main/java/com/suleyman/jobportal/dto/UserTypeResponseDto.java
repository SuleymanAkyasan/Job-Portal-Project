package com.suleyman.jobportal.dto;

import com.suleyman.jobportal.entity.UsersType;

public class UserTypeResponseDto {

    private int userTypeId;
    private String userTypeName;

    public UserTypeResponseDto(UsersType userType) {
        this.userTypeId = userType.getUserTypeId();
        this.userTypeName = userType.getUserTypeName();
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }
}