package com.teamsolution.inventory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CloudinaryFolderType {
  PRODUCT("products"),
  ;

  private final String key;
}
