package com.teamsolution.demo.authservice.controller;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.service.AuthService;
import com.teamsolution.demo.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/auth/accounts")
public class AuthController extends BaseController<Account, UUID> {
    public AuthController(AuthService service) {
        super(service);
    }
}