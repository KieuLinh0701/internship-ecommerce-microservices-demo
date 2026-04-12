package com.teamsolution.inventory.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.inventory.enums.AttributeValueStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_attribute_value")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AttributeValue extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attribute_id")
  private Attribute attribute;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private String code;

  @Column(name = "description")
  private String description;

  @Builder.Default private Integer sortOrder = 0;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AttributeValueStatus status = AttributeValueStatus.ACTIVE;
}
