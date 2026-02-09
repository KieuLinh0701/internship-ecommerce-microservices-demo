package com.teamsolution.demo.customerservice.controller;

import com.teamsolution.demo.common.base.BaseController;
import com.teamsolution.demo.customerservice.entity.Address;
import com.teamsolution.demo.customerservice.service.AddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
public class AddressController extends BaseController<Address, Long> {
    public AddressController(AddressService service) {
        super(service);
    }
}
