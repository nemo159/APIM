package com.study.apim.memberservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.study.apim.memberservice.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    Optional<Member> findByNameIgnoreCaseAndDelYn(String name, String delYn);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByPhoneHash(String phoneHash);
    boolean existsByPhoneHashAndIdNot(String phoneHash, Long id);
}
