package com.teamsolution.demo.authservice.controller;

import com.teamsolution.demo.authservice.entity.Account;
import com.teamsolution.demo.authservice.service.AuthService;
import com.teamsolution.demo.common.base.BaseController;
import com.teamsolution.demo.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth/accounts")
public class AuthController extends BaseController<Account, Long> {
    public AuthController(AuthService service) {
        super(service);
    }
}