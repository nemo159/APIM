package com.study.apim.memberservice.domain;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_encrypted")
    private String phoneEncrypted;

    @Column(name = "phone_hash", length = 64)
    private String phoneHash;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired = true;

    @Column(name = "del_yn", nullable = false, length = 1)
    private String delYn = "Y";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "member_roles",
        joinColumns = @JoinColumn(name = "member_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    protected Member() {}

    public static Member create(String name, String phoneEncrypted, String phoneHash,
                                String passwordHash, String actor, Role memberRole) {
        Member member = new Member();
        OffsetDateTime now = OffsetDateTime.now();
        member.name = name;
        member.phoneEncrypted = phoneEncrypted;
        member.phoneHash = phoneHash;
        member.passwordHash = passwordHash;
        member.passwordChangeRequired = true;
        member.delYn = "Y";
        member.createdAt = now;
        member.updatedAt = now;
        member.createdBy = actor;
        member.updatedBy = actor;
        member.roles.add(memberRole);
        return member;
    }

    public void changePhone(String encryptedPhone, String phoneHash, String actor) {
        this.phoneEncrypted = encryptedPhone;
        this.phoneHash = phoneHash;
        touch(actor);
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordChangeRequired = false;
        touch(this.name);
    }

    public void softDelete(String actor) {
        this.delYn = "N";
        touch(actor);
    }

    public void replaceAssignableRoles(Set<Role> selectedRoles, String actor) {
        this.roles.removeIf(role -> role.getCode() != RoleCode.MASTER);
        this.roles.addAll(selectedRoles);
        touch(actor);
    }

    private void touch(String actor) {
        this.updatedAt = OffsetDateTime.now();
        this.updatedBy = actor;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhoneEncrypted() { return phoneEncrypted; }
    public String getPhoneHash() { return phoneHash; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isPasswordChangeRequired() { return passwordChangeRequired; }
    public String getDelYn() { return delYn; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public Set<Role> getRoles() { return roles; }
    public boolean hasRole(RoleCode roleCode) { return roles.stream().anyMatch(role -> role.getCode() == roleCode); }
}
