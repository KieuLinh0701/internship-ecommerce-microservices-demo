package com.teamsolution.demo.customerservice.service;

import com.teamsolution.demo.common.base.BaseServiceImpl;
import com.teamsolution.demo.customerservice.entity.Address;
import com.teamsolution.demo.customerservice.entity.Customer;
import com.teamsolution.demo.customerservice.repository.AddressRepository;
import com.teamsolution.demo.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends BaseServiceImpl<Address, Long> {
    public AddressService(AddressRepository repository) {
        super(repository);
    }
}
