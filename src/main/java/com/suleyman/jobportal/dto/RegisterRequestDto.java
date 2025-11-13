package com.suleyman.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {

    @Email(message = "Geçerli bir e-posta adresi girin")
    @NotEmpty(message = "E-posta boş olamaz")
    private String email;

    @NotEmpty(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    @NotNull(message = "Kullanıcı tipi ID'si boş olamaz")
    private Integer userTypeId;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getUserTypeId() { return userTypeId; }
    public void setUserTypeId(Integer userTypeId) { this.userTypeId = userTypeId; }
}