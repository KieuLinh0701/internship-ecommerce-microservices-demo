package com.teamsolution.customer.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "customer_addresses")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Address extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 20)
  private String phone;

  @Column(name = "city_code", nullable = false)
  private Integer cityCode;

  @Column(name = "city_name", nullable = false, length = 100)
  private String cityName;

  @Column(name = "ward_code", nullable = false)
  private Integer wardCode;

  @Column(name = "ward_name", nullable = false, length = 100)
  private String wardName;

  @Column(nullable = false)
  private String detail;

  @Column(name = "is_default", nullable = false)
  @Builder.Default
  private Boolean isDefault = false;
}
