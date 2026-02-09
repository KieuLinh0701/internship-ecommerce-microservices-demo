package com.teamsolution.demo.customerservice.entity;

import com.teamsolution.demo.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
@EqualsAndHashCode(callSuper = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Address> addresses = new HashSet<>();
}
