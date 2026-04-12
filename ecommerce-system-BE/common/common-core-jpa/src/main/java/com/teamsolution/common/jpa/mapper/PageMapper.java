package com.teamsolution.common.jpa.mapper;

import com.teamsolution.common.core.dto.common.response.PageResponse;
import org.springframework.data.domain.Page;

public class PageMapper {

  public static <T> PageResponse<T> toPageResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }
}
