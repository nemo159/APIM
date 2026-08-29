package com.study.apim.memberservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.apim.memberservice.domain.Member;
import com.study.apim.memberservice.domain.Role;
import com.study.apim.memberservice.domain.RoleCode;
import com.study.apim.memberservice.repository.MemberRepository;
import com.study.apim.memberservice.repository.RoleRepository;
import com.study.apim.memberservice.security.CryptoService;
import com.study.apim.memberservice.web.MemberForm;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final CryptoService cryptoService;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, RoleRepository roleRepository,
                         CryptoService cryptoService, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.cryptoService = cryptoService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<MemberView> search(String name, String phone, int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 10, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Member> specification = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            String keyword = "%" + name.trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), keyword));
        }
        if (phone == null || phone.isBlank()) {
            return memberRepository.findAll(specification, pageable).map(this::toView);
        }

        String phoneKeyword = normalizePhoneKeyword(phone);
        List<MemberView> matched = memberRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "id"))
            .stream()
            .map(this::toView)
            .filter(member -> member.phone().replace("-", "").contains(phoneKeyword))
            .toList();
        int start = Math.min((int) pageable.getOffset(), matched.size());
        int end = Math.min(start + pageable.getPageSize(), matched.size());
        return new PageImpl<>(matched.subList(start, end), pageable, matched.size());
    }

    public MemberView get(Long id) {
        return toView(find(id));
    }

    public List<MemberView> findAllForRoleManagement() {
        return memberRepository.findAll(Sort.by("name")).stream().map(this::toView).toList();
    }

    @Transactional
    public Long create(MemberForm form, String actor) {
        String name = form.getName().trim();
        String phone = normalizePhone(form.getPhone());
        String phoneHash = cryptoService.hashForSearch(phone);
        if (memberRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("이미 등록된 이름입니다.");
        }
        if (memberRepository.existsByPhoneHash(phoneHash)) {
            throw new IllegalArgumentException("이미 등록된 핸드폰 번호입니다.");
        }
        Role memberRole = roleRepository.findByCode(RoleCode.MEMBER)
            .orElseThrow(() -> new IllegalStateException("MEMBER 권한이 없습니다."));
        Member member = Member.create(name, cryptoService.encrypt(phone), phoneHash,
            passwordEncoder.encode(phone), actor, memberRole);
        return memberRepository.save(member).getId();
    }

    @Transactional
    public void updatePhone(Long id, String phoneInput, String actor) {
        String phone = normalizePhone(phoneInput);
        String hash = cryptoService.hashForSearch(phone);
        if (memberRepository.existsByPhoneHashAndIdNot(hash, id)) {
            throw new IllegalArgumentException("이미 등록된 핸드폰 번호입니다.");
        }
        Member member = find(id);
        protectMaster(member, actor);
        member.changePhone(cryptoService.encrypt(phone), hash, actor);
    }

    @Transactional
    public void delete(Long id, String actor) {
        Member member = find(id);
        protectFromDeletion(member, actor);
        member.softDelete(actor);
    }

    @Transactional
    public int deleteAll(List<Long> ids, String actor) {
        List<Member> deletableMembers = memberRepository.findAllById(ids).stream()
            .filter(member -> !member.hasRole(RoleCode.MASTER))
            .filter(member -> !member.getName().equalsIgnoreCase(actor))
            .filter(member -> "Y".equals(member.getDelYn()))
            .toList();
        deletableMembers.forEach(member -> {
            member.softDelete(actor);
        });
        return deletableMembers.size();
    }

    @Transactional
    public void updateRoles(Long memberId, Set<RoleCode> requestedRoles, String actor) {
        Member member = find(memberId);
        if (member.hasRole(RoleCode.MASTER)) {
            throw new IllegalArgumentException("MASTER 권한 회원은 화면에서 권한을 변경할 수 없습니다.");
        }
        Set<RoleCode> allowed = new HashSet<>(requestedRoles);
        allowed.retainAll(Set.of(RoleCode.ADMIN, RoleCode.MEMBER));
        if (allowed.size() != 1) {
            throw new IllegalArgumentException("ADMIN 또는 MEMBER 권한 중 하나를 선택해 주세요.");
        }
        Set<Role> roles = new HashSet<>(roleRepository.findByCodeIn(allowed));
        member.replaceAssignableRoles(roles, actor);
    }

    private Member find(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private void protectMaster(Member member, String actor) {
        if (member.hasRole(RoleCode.MASTER) && !member.getName().equals(actor)) {
            throw new IllegalArgumentException("MASTER 회원 정보는 MASTER 본인만 변경할 수 있습니다.");
        }
    }

    private void protectFromDeletion(Member member, String actor) {
        if (member.hasRole(RoleCode.MASTER)) {
            throw new IllegalArgumentException("MASTER 계정은 삭제할 수 없습니다.");
        }
        if (member.getName().equalsIgnoreCase(actor)) {
            throw new IllegalArgumentException("현재 로그인한 본인 계정은 삭제할 수 없습니다.");
        }
    }

    private MemberView toView(Member member) {
        Set<RoleCode> roles = member.getRoles().stream().map(Role::getCode).collect(java.util.stream.Collectors.toSet());
        return new MemberView(member.getId(), member.getName(), cryptoService.decrypt(member.getPhoneEncrypted()),
            member.getDelYn(), member.getCreatedAt(), member.getUpdatedAt(), member.getCreatedBy(), member.getUpdatedBy(), roles);
    }

    public static String normalizePhone(String phone) {
        String digits = phone == null ? "" : phone.replaceAll("[^0-9]", "");
        if (digits.length() < 10 || digits.length() > 11) {
            throw new IllegalArgumentException("핸드폰 번호는 숫자 10~11자리여야 합니다.");
        }
        return digits;
    }

    private static String normalizePhoneKeyword(String phone) {
        String keyword = phone == null ? "" : phone.trim();
        if (!keyword.matches("[0-9]{1,11}")) {
            throw new IllegalArgumentException("핸드폰 번호 검색어는 하이픈 없이 숫자 1~11자리로 입력해 주세요.");
        }
        return keyword;
    }
}
