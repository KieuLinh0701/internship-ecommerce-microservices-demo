package com.teamsolution.inventory.entity;

import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.inventory.enums.BrandStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_brand")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Brand extends BaseEntity {

  @Column(name = "name")
  private String name;

  @Column(name = "slug")
  private String slug;

  @Column(name = "logo_url")
  private String logoUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private BrandStatus status = BrandStatus.ACTIVE;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
  private List<Product> products;
}
