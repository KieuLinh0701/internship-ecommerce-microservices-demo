package com.teamsolution.common.kafka.event.inventory;

import com.teamsolution.common.kafka.event.BaseEvent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductChangedEvent extends BaseEvent {
  private String productId;
  private String name;
  private String description;

  private String categoryId;
  private String categoryName;

  private String brandId;
  private String brandName;

  private Long minPrice;
  private Long maxPrice;

  private String status;

  private String thumbnail;

  private LocalDateTime createdAt;
}
