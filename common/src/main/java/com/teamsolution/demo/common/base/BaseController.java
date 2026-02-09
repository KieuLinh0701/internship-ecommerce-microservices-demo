package com.teamsolution.demo.common.base;

import com.teamsolution.demo.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<T, ID> {

    protected final BaseService<T, ID> service;

    protected BaseController(BaseService<T, ID> service) {
        this.service = service;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<ApiResponse<T>> create(@RequestBody T entity) {
        return ResponseEntity.ok(ApiResponse.success(service.create(entity)));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @RequestBody T entity) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, entity)));
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable ID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<ApiResponse<List<T>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }
}
