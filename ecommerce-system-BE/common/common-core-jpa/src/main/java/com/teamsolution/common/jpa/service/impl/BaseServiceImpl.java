package com.teamsolution.common.jpa.service.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.common.jpa.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<E, D, ID>
        implements BaseService<E, D, ID> {

    protected final JpaRepository<E, ID> repository;
    protected final BaseMapper<E, D> mapper;

    @Override
    public E getEntityById(ID id) {
        return findEntityById(id);
    }

    @Override
    public D getById(ID id) {
        return mapper.toDto(findEntityById(id));
    }

    @Override
    public E saveEntity(E entity) {
        return repository.save(entity);
    }

    @Override
    public List<E> saveAllEntity(List<E> entity) {
        return repository.saveAll(entity);
    }

    @Override
    public D save(E entity) {
        return mapper.toDto(saveEntity(entity));
    }

    @Override
    public E createEntity(E entity) {
        return saveEntity(entity);
    }

    @Override
    public D create(E entity) {
        return mapper.toDto(createEntity(entity));
    }

    @Override
    public E updateEntity(ID id, E entity) {
        findEntityById(id);
        return saveEntity(entity);
    }

    @Override
    public D update(ID id, E entity) {
        return mapper.toDto(updateEntity(id, entity));
    }

    @Override
    public void delete(ID id) {
        checkExists(id);
        repository.deleteById(id);
    }

    @Override
    public void checkExists(ID id) {
        if (!repository.existsById(id)) {
            throw new AppException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private E findEntityById(ID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }
}
