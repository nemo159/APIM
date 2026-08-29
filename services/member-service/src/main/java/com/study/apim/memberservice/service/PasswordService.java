package com.study.apim.memberservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.apim.memberservice.repository.MemberRepository;

@Service
public class PasswordService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void change(String username, String currentPassword, String newPassword, String confirmation) {
        var member = memberRepository.findByNameIgnoreCaseAndDelYn(username, "Y")
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (!passwordEncoder.matches(currentPassword, member.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (!newPassword.equals(confirmation)) {
            throw new IllegalArgumentException("새 비밀번호 확인이 일치하지 않습니다.");
        }
        if (newPassword.length() < 8 || newPassword.length() > 72) {
            throw new IllegalArgumentException("새 비밀번호는 8~72자로 입력해 주세요.");
        }
        if (passwordEncoder.matches(newPassword, member.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호와 다른 비밀번호를 사용해 주세요.");
        }
        member.changePassword(passwordEncoder.encode(newPassword));
    }
}
