package com.teamsolution.demo.common.mapper;

import java.util.List;
import org.mapstruct.MappingTarget;

public interface BaseMapper<E, D> {
  E toEntity(D dto);

  D toDto(E entity);

  void updateEntityFromDto(D dto, @MappingTarget E entity);

  List<D> toDtoList(List<E> entities);
}
