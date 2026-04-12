package com.teamsolution.auth.entity;

import com.teamsolution.auth.enums.AccountLockReason;
import com.teamsolution.common.core.enums.auth.AccountStatus;
import com.teamsolution.common.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private AccountStatus status = AccountStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(name = "lock_reason")
  @Builder.Default
  private AccountLockReason lockReason = AccountLockReason.NONE;

  @Column(name = "failed_login_attempts")
  @Builder.Default
  private int failedLoginAttempts = 0;

  @Column(name = "locked_until")
  private LocalDateTime lockedUntil;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Builder.Default
  @OneToMany(mappedBy = "account")
  private Set<AccountRole> accountRoles = new HashSet<>();
}
