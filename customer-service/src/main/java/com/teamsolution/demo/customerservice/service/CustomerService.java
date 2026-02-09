package com.teamsolution.demo.customerservice.service;

import com.teamsolution.demo.common.base.BaseServiceImpl;
import com.teamsolution.demo.customerservice.entity.Customer;
import com.teamsolution.demo.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends BaseServiceImpl<Customer, Long> {
    public CustomerService(CustomerRepository repository) {
        super(repository);
    }
}
