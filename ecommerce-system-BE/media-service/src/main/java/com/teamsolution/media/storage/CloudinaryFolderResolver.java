package com.teamsolution.media.storage;

import com.teamsolution.media.config.properties.CloudinaryProperties;
import com.teamsolution.common.core.enums.media.CloudinaryFolderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloudinaryFolderResolver {

  private final CloudinaryProperties properties;

  public String getFolder(CloudinaryFolderType type) {
    return properties.getFolders().get(type.getKey());
  }
}
