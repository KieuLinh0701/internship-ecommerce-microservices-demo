package com.teamsolution.demo.customerservice.service;

import com.teamsolution.demo.common.base.BaseServiceImpl;
import com.teamsolution.demo.customerservice.entity.Customer;
import com.teamsolution.demo.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService extends BaseServiceImpl<Customer, UUID> {
    public CustomerService(CustomerRepository repository) {
        super(repository);
    }
}
