package com.teamsolution.demo.customerservice.repository;

import com.teamsolution.demo.common.base.BaseRepository;
import com.teamsolution.demo.customerservice.entity.Customer;
import java.util.UUID;

public interface CustomerRepository extends BaseRepository<Customer, UUID> {}
