package com.teamsolution.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminBffApplication {
  public static void main(String[] args) {
    SpringApplication.run(AdminBffApplication.class, args);
  }
}
