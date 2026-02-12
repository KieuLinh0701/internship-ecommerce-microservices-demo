package com.teamsolution.demo.common.service.impl;

import com.teamsolution.demo.common.exception.ResourceNotFoundException;
import com.teamsolution.demo.common.mapper.BaseMapper;
import com.teamsolution.demo.common.service.BaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<E, D, ID> implements BaseService<D, ID> {

  protected final JpaRepository<E, ID> repository;
  protected final BaseMapper<E, D> mapper;

  @Override
  public D create(D dto) {
    E entity = mapper.toEntity(dto);
    E saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  public D update(ID id, D dto) {
    E entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
    mapper.updateEntityFromDto(dto, entity);
    E saved = repository.save(entity);
    return mapper.toDto(saved);
  }

  @Override
  public void delete(ID id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Entity not found with id: " + id);
    }
    repository.deleteById(id);
  }

  @Override
  public D getById(ID id) {
    E entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));

    return mapper.toDto(entity);
  }

  @Override
  public List<D> getAll() {
    List<E> entities = repository.findAll();
    return mapper.toDtoList(entities);
  }
}
