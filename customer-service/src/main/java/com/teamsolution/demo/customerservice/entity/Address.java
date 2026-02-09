package com.teamsolution.demo.customerservice.entity;

import com.teamsolution.demo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@EqualsAndHashCode(callSuper = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address extends BaseEntity  {

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
