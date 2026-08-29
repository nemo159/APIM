package com.study.apim.memberservice.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute("isMaster")
    public boolean isMaster(Authentication authentication) {
        return hasRole(authentication, "ROLE_MASTER");
    }

    @ModelAttribute("canManageMembers")
    public boolean canManageMembers(Authentication authentication) {
        return hasRole(authentication, "ROLE_MASTER") || hasRole(authentication, "ROLE_ADMIN");
    }

    @ModelAttribute("loginName")
    public String loginName(Authentication authentication) {
        return authentication == null ? "" : authentication.getName();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
