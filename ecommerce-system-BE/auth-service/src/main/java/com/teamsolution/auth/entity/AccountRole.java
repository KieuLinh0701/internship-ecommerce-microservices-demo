package com.teamsolution.auth.entity;

import com.teamsolution.auth.entity.id.AccountRoleId;
import com.teamsolution.auth.enums.AccountRoleStatus;
import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.util.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_roles")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AccountRole {

  @EmbeddedId @Builder.Default private AccountRoleId id = new AccountRoleId();

  @Column(name = "surrogate_id")
  @Builder.Default
  private UUID surrogateId = UuidUtils.generate();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("accountId")
  @JoinColumn(name = "account_id")
  private Account account;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_id")
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private AccountRoleStatus status = AccountRoleStatus.ACTIVE;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private UUID createdBy;

  @LastModifiedBy
  @Column(name = "updated_by")
  private UUID updatedBy;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

  private UUID deletedBy;

  private LocalDateTime deletedAt;

  @Enumerated(EnumType.STRING)
  private StatusChangeReason statusChangeReason;

  @Version
  @Column(name = "version")
  private Long version;
}
