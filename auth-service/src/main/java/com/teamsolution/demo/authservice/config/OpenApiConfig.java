package com.teamsolution.demo.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "TeamSolution E-Commerce API",
            version = "v1.0",
            description = "API documentation for internship microservices project",
            contact = @Contact(name = "TeamSolution", email = "support@teamsolution.com")),
    servers = {
      @Server(url = "http://localhost:9001", description = "Local"),
      @Server(url = "https://api.teamsolution.com", description = "Production")
    })
public class OpenApiConfig {}
