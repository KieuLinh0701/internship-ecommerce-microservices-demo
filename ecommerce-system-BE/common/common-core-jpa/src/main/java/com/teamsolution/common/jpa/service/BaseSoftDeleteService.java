package com.teamsolution.common.jpa.service;

public interface BaseSoftDeleteService <E, D, ID> extends BaseService<E, D, ID> {

    E getEntityByIdIncludingDeleted(ID id);
    public D getByIdIncludingDeleted(ID id);
    void checkExistsIncludingDeleted(ID id);
}