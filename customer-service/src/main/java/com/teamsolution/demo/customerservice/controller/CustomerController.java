package com.teamsolution.demo.customerservice.controller;

import com.teamsolution.demo.common.base.BaseController;
import com.teamsolution.demo.customerservice.entity.Customer;
import com.teamsolution.demo.customerservice.service.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController<Customer, UUID> {
    public CustomerController(CustomerService service) {
        super(service);
    }
}
