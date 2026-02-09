package com.teamsolution.demo.authservice.entity;

import com.teamsolution.demo.authservice.enums.AccountStatus;
import com.teamsolution.demo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "accounts")
@EqualsAndHashCode(callSuper = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status = AccountStatus.INACTIVE;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountRole> accountRoles;
}
