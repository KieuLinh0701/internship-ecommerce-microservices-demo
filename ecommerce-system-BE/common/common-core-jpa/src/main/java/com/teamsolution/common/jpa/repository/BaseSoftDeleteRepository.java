package com.teamsolution.common.jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseSoftDeleteRepository<E, ID>
        extends BaseRepository<E, ID> {

    Optional<E> findByIdAndIsDeletedFalse(ID id);
    boolean existsByIdAndIsDeletedFalse(ID id);
}