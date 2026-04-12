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
public class AddressResponse {
  private UUID id;
  private String name;
  private String phone;
  private Integer cityCode;
  private String cityName;
  private Integer wardCode;
  private String wardName;
  private String detail;
  private Boolean isDefault;
}
