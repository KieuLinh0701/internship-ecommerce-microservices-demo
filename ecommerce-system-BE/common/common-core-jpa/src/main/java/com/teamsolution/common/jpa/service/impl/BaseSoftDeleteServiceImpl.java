package com.teamsolution.common.jpa.service.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.common.jpa.entity.BaseEntity;
import com.teamsolution.common.jpa.repository.BaseSoftDeleteRepository;
import com.teamsolution.common.jpa.service.BaseSoftDeleteService;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseSoftDeleteServiceImpl<E extends BaseEntity, D, ID>
        extends BaseServiceImpl<E, D, ID>
        implements BaseSoftDeleteService<E, D, ID> {

    protected final BaseSoftDeleteRepository<E, ID> softDeleteRepository;

    protected BaseSoftDeleteServiceImpl(
            JpaRepository<E, ID> repository,
            BaseMapper<E, D> mapper,
            BaseSoftDeleteRepository<E, ID> softDeleteRepository) {
        super(repository, mapper);
        this.softDeleteRepository = softDeleteRepository;
    }

    private E findEntityById(ID id) {
        return softDeleteRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private E findEntityByIdIncludingDeleted(ID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public void checkExists(ID id) {
        if (!softDeleteRepository.existsByIdAndIsDeletedFalse(id)) {
            throw new AppException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void checkExistsIncludingDeleted(ID id) {
        if (!repository.existsById(id)) {
            throw new AppException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public E getEntityByIdIncludingDeleted(ID id) {
        return findEntityByIdIncludingDeleted(id);
    }

    @Override
    public D getByIdIncludingDeleted(ID id) {
        return mapper.toDto(findEntityByIdIncludingDeleted(id));
    }
}
