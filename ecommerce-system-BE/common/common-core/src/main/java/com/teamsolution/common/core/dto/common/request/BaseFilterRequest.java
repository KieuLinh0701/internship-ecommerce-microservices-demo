package com.teamsolution.common.core.dto.common.request;

import lombok.Data;

@Data
public class BaseFilterRequest {
  private int page = 0;
  private int size = 10;
  private String sortBy = "createdAt";
  private String direction = "desc";
}
