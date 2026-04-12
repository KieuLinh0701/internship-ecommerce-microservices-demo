package com.teamsolution.inventory.service.internal.impl;

import com.teamsolution.inventory.entity.AttributeValue;
import com.teamsolution.inventory.enums.AttributeValueStatus;
import com.teamsolution.inventory.repository.AttributeValueRepository;
import com.teamsolution.inventory.service.internal.AttributeValueInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributeValueInternalServiceImpl implements AttributeValueInternalService {

  private final AttributeValueRepository attributeValueRepository;

  @Override
  public List<AttributeValue> findValidActiveByIds(List<UUID> ids) {
    return attributeValueRepository.findValidActiveByIds(ids, List.of(AttributeValueStatus.ACTIVE));
  }
}
