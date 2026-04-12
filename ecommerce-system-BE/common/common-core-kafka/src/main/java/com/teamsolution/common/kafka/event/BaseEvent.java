package com.teamsolution.common.kafka.event;

import com.teamsolution.common.core.util.UuidUtils;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BaseEvent {

  @Builder.Default private UUID id = UuidUtils.generate();

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}
