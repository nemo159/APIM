package com.study.apim.memberservice.web;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.apim.memberservice.service.MemberService;

@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String list(@RequestParam(name = "name", defaultValue = "") String name,
                       @RequestParam(name = "phone", defaultValue = "") String phone,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("members", memberService.search(name, phone, page));
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        return "members/list";
    }

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("form", new MemberForm());
        return "members/form";
    }

    @PostMapping("/members")
    public String create(@Valid @ModelAttribute("form") MemberForm form,
                         BindingResult bindingResult, Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "members/form";
        }
        try {
            Long id = memberService.create(form, principal.getName());
            redirectAttributes.addFlashAttribute("message", "회원이 등록되었습니다. 초기 비밀번호는 핸드폰 번호입니다.");
            return "redirect:/members/" + id;
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("member", exception.getMessage());
            return "members/form";
        }
    }

    @GetMapping("/members/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("member", memberService.get(id));
        return "members/detail";
    }

    @PostMapping("/members/{id}/phone")
    public String updatePhone(@PathVariable("id") Long id, @RequestParam("phone") String phone,
                              Principal principal, RedirectAttributes redirectAttributes) {
        try {
            memberService.updatePhone(id, phone, principal.getName());
            redirectAttributes.addFlashAttribute("message", "핸드폰 번호가 수정되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/members/" + id;
    }

    @PostMapping("/members/{id}/delete")
    public String delete(@PathVariable("id") Long id, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            memberService.delete(id, principal.getName());
            redirectAttributes.addFlashAttribute("message", "회원이 삭제 처리되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/members";
    }

    @PostMapping("/members/delete")
    public String deleteAll(@RequestParam(name = "ids", required = false) List<Long> ids,
                            Principal principal, RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제할 회원을 선택해 주세요.");
        } else {
            try {
                int deletedCount = memberService.deleteAll(ids, principal.getName());
                int protectedCount = ids.size() - deletedCount;
                String message = deletedCount + "명이 삭제 처리되었습니다.";
                if (protectedCount > 0) {
                    message += " MASTER 또는 현재 로그인한 본인 계정 " + protectedCount + "명은 제외되었습니다.";
                }
                redirectAttributes.addFlashAttribute("message", message);
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            }
        }
        return "redirect:/members";
    }
}
