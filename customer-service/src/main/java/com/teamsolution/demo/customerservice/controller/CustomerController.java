package com.teamsolution.demo.customerservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  // test
  @GetMapping("/info")
  public Map<String, Object> getCustomerInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    Map<String, Object> response = new HashMap<>();
    response.put("message", "This is protected customer info!");
    response.put("user", auth.getName());
    response.put("authorities", auth.getAuthorities());

    return response;
  }
}
