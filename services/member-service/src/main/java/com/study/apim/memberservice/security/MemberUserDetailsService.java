package com.study.apim.memberservice.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.study.apim.memberservice.repository.MemberRepository;

@Service
public class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public MemberUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var member = memberRepository.findByNameIgnoreCaseAndDelYn(username.trim(), "Y")
            .orElseThrow(() -> new UsernameNotFoundException("이름 또는 비밀번호를 확인해 주세요."));
        String[] authorities = member.getRoles().stream()
            .map(role -> "ROLE_" + role.getCode().name())
            .toArray(String[]::new);
        return User.withUsername(member.getName())
            .password(member.getPasswordHash())
            .authorities(authorities)
            .build();
    }
}
