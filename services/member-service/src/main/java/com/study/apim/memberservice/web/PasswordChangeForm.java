package com.study.apim.memberservice.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeForm {
    @NotBlank(message = "이름을 입력해 주세요.")
    private String username;

    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 72, message = "새 비밀번호는 8~72자로 입력해 주세요.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
    private String newPasswordConfirm;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getNewPasswordConfirm() { return newPasswordConfirm; }
    public void setNewPasswordConfirm(String value) { this.newPasswordConfirm = value; }
}
