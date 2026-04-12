package com.teamsolution.inventory.dto.request;

import com.teamsolution.common.core.dto.common.request.BaseFilterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BrandFilterRequest extends BaseFilterRequest {

  private String keyword;
}
