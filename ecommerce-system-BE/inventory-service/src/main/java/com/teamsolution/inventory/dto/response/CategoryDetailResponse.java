package com.teamsolution.inventory.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailResponse {

  private UUID id;
  private String name;
  private String slug;
  private String imageUrl;
  private List<CategoryDetailResponse> children;
}
