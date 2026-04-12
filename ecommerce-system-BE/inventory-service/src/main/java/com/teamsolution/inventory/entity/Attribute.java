package com.teamsolution.inventory.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.inventory.enums.AttributeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_attribute")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Attribute extends BaseEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(nullable = false, unique = true, length = 20)
  private String code;

  @Builder.Default private Integer sortOrder = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private AttributeStatus status = AttributeStatus.ACTIVE;
}
