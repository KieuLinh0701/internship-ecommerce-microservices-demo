package com.teamsolution.common.jpa.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

  public static Pageable toPageable(
      int page, int size, String sortBy, String direction, boolean isNative) {

    String columnOrField = isNative ? toSnakeCase(sortBy) : sortBy;

    Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);

    Sort sort = Sort.by(dir, columnOrField);

    return PageRequest.of(page, size, sort);
  }

  private static String toSnakeCase(String input) {
    return input.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
  }
}
