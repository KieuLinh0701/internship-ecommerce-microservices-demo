package com.teamsolution.demo.customerservice.entity;

import com.teamsolution.demo.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
  private boolean isDefault = false;
}
