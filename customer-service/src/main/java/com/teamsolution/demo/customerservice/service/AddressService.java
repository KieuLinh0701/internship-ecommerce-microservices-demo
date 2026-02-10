package com.teamsolution.demo.customerservice.service;

import com.teamsolution.demo.common.base.BaseServiceImpl;
import com.teamsolution.demo.customerservice.entity.Address;
import com.teamsolution.demo.customerservice.repository.AddressRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends BaseServiceImpl<Address, UUID> {
  public AddressService(AddressRepository repository) {
    super(repository);
  }
}
