package com.teamsolution.common.kafka.event.inventory;

import com.teamsolution.common.kafka.event.BaseEvent;
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
public class ProductStatusChangedEvent extends BaseEvent {
  private String productId;
  private String status;
}
