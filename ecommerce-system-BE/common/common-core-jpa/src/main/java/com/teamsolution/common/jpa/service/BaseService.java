package com.teamsolution.common.jpa.service;

import java.util.List;

public interface BaseService<E, D, ID> {

    E getEntityById(ID id);

    D getById(ID id);

    E createEntity(E entity);

    D create(E entity);

    E updateEntity(ID id, E entity);

    D update(ID id, E entity);

    E saveEntity(E entity);

    D save(E entity);

    List<E> saveAllEntity(List<E> entity);

    void delete(ID id);

    void checkExists(ID id);
}
