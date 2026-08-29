package com.study.apim.memberservice.web;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.apim.memberservice.service.PasswordService;

@Controller
public class AuthController {
    private final PasswordService passwordService;

    public AuthController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PasswordChangeForm());
        }
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/members";
    }

    @GetMapping("/password/change")
    public String passwordChange() {
        return "redirect:/login?changePassword";
    }

    @PostMapping("/password/change")
    public String passwordChange(@Valid @ModelAttribute("form") PasswordChangeForm form,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("openPasswordModal", true);
            return "login";
        }
        try {
            passwordService.change(form.getUsername().trim(), form.getCurrentPassword(),
                form.getNewPassword(), form.getNewPasswordConfirm());
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("password", exception.getMessage());
            model.addAttribute("openPasswordModal", true);
            return "login";
        }
        redirectAttributes.addFlashAttribute("passwordChangedMessage", "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요.");
        return "redirect:/login";
    }

    @PostMapping("/account/password/change")
    public String authenticatedPasswordChange(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPasswordConfirm") String newPasswordConfirm,
            Principal principal, RedirectAttributes redirectAttributes) {
        try {
            passwordService.change(principal.getName(), currentPassword, newPassword, newPasswordConfirm);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("passwordError", exception.getMessage());
        }
        return "redirect:/members";
    }
}
