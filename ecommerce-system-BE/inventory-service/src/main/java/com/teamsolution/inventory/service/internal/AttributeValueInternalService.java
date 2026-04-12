package com.teamsolution.inventory.service.internal;

import com.teamsolution.inventory.entity.AttributeValue;
import java.util.List;
import java.util.UUID;

public interface AttributeValueInternalService {

  List<AttributeValue> findValidActiveByIds(List<UUID> ids);
}
