package com.teamsolution.common.core.enums.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CloudinaryFolderType {
  PRODUCT("products"),
  ;

  private final String key;
}
