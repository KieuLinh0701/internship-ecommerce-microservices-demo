package com.teamsolution.customer.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
  private UUID id;
  private String fullName;
  private String phone;
  private String avatarUrl;
}
