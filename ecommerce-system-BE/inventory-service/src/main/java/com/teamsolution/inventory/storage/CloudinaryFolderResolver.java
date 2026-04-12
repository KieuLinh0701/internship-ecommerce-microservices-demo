package com.teamsolution.inventory.storage;

import com.teamsolution.inventory.config.properties.CloudinaryProperties;
import com.teamsolution.inventory.enums.CloudinaryFolderType;
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
