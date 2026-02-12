package com.teamsolution.demo.common.base.controller;

import com.teamsolution.demo.common.base.service.BaseService;
import com.teamsolution.demo.common.response.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<D, ID> {

  protected final BaseService<D, ID> service;

  protected BaseController(BaseService<D, ID> service) {
    this.service = service;
  }

  @PostMapping(produces = "application/json")
  public ResponseEntity<ApiResponse<D>> create(@RequestBody D dto) {
    return ResponseEntity.ok(ApiResponse.success(service.create(dto)));
  }

  @PutMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<ApiResponse<D>> update(@PathVariable ID id, @RequestBody D dto) {
    return ResponseEntity.ok(ApiResponse.success(service.update(id, dto)));
  }

  @DeleteMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
    service.delete(id);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<ApiResponse<D>> getById(@PathVariable ID id) {
    return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
  }

  @GetMapping(produces = "application/json")
  public ResponseEntity<ApiResponse<List<D>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(service.getAll()));
  }
}
