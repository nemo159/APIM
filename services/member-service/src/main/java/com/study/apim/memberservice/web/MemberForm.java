package com.study.apim.memberservice.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MemberForm {
    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 100, message = "이름은 100자 이하로 입력해 주세요.")
    private String name;

    @NotBlank(message = "핸드폰 번호를 입력해 주세요.")
    @Pattern(regexp = "^[0-9-]{10,13}$", message = "핸드폰 번호 형식을 확인해 주세요.")
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
