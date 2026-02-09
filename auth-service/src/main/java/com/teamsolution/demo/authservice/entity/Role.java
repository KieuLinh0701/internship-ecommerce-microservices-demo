package com.teamsolution.demo.authservice.entity;

import com.teamsolution.demo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountRole> accountRoles;
}
