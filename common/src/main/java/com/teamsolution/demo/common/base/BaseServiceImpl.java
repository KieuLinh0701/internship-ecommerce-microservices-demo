package com.teamsolution.demo.common.base;

import com.teamsolution.demo.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@RequiredArgsConstructor
public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {
    protected  final JpaRepository<T, ID> repository;

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id: " + id);
        }
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }
}
