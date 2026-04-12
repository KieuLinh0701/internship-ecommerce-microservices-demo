package com.teamsolution.inventory.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.inventory.entity.AttributeValue;
import com.teamsolution.inventory.enums.AttributeValueStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttributeValueRepository extends BaseRepository<AttributeValue, UUID> {

  @Query(
      """
    SELECT av FROM AttributeValue av
    WHERE av.id IN :ids
      AND av.isDeleted = false
      AND av.status in :statuses
""")
  List<AttributeValue> findValidActiveByIds(
      @Param("ids") List<UUID> ids, @Param("statuses") List<AttributeValueStatus> statuses);
}
