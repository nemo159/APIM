package com.study.apim.memberservice.web;

import java.security.Principal;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.apim.memberservice.domain.RoleCode;
import com.study.apim.memberservice.service.MemberService;

@Controller
public class RoleAdminController {
    private final MemberService memberService;

    public RoleAdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/admin/roles")
    public String roles(Model model) {
        model.addAttribute("members", memberService.findAllForRoleManagement());
        return "admin/roles";
    }

    @PostMapping("/admin/roles/{memberId}")
    public String update(@PathVariable("memberId") Long memberId,
                         @RequestParam("role") RoleCode role,
                         Principal principal, RedirectAttributes redirectAttributes) {
        if (role == RoleCode.MASTER) {
            throw new IllegalArgumentException("MASTER 권한은 화면에서 변경할 수 없습니다.");
        }
        try {
            memberService.updateRoles(memberId, Set.of(role), principal.getName());
            redirectAttributes.addFlashAttribute("message", "권한이 변경되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/roles";
    }
}
