package com.teamsolution.customer.repository;

import com.teamsolution.common.jpa.repository.BaseRepository;
import com.teamsolution.customer.entity.Customer;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends BaseRepository<Customer, UUID> {
  Optional<Customer> findByAccountIdAndIsDeletedFalse(UUID accountId);

  boolean existsByAccountIdAndIsDeletedFalse(UUID accountId);

  Optional<Customer> findByIdAndIsDeletedFalse(UUID id);
}
