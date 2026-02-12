package com.teamsolution.demo.common.service;

import java.util.List;

public interface BaseService<D, ID> {
  D create(D dto);

  D update(ID id, D dto);

  void delete(ID id);

  D getById(ID id);

  List<D> getAll();
}
