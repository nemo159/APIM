package com.study.apim.memberservice.service;

import java.time.OffsetDateTime;
import java.util.Set;

import com.study.apim.memberservice.domain.RoleCode;

public record MemberView(
    Long id,
    String name,
    String phone,
    String delYn,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Set<RoleCode> roles
) {
    public MemberView {
        phone = formatPhone(phone);
    }

    public boolean hasRole(String code) {
        return roles.stream().anyMatch(role -> role.name().equals(code));
    }

    private static String formatPhone(String value) {
        if (value == null) return "";
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        if (digits.length() == 10) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }
        return value;
    }
}
