package com.study.apim.memberservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.apim.memberservice.domain.Role;
import com.study.apim.memberservice.domain.RoleCode;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
    List<Role> findByCodeIn(Collection<RoleCode> codes);
}
