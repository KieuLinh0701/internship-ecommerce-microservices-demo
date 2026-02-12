package com.teamsolution.demo.authservice.entity;

import com.teamsolution.demo.authservice.enums.RoleStatus;
import com.teamsolution.demo.common.entity.BaseEntity;
import jakarta.persistence.Column;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private RoleStatus status = RoleStatus.ACTIVE;

  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AccountRole> accountRoles;
}
