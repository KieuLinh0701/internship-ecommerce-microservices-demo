package com.teamsolution.common.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class OpenApiAutoConfiguration {

  @Value("${openapi.title:Service API}")
  private String title;

  @Value("${openapi.description:Service description}")
  private String description;

  @Value("${openapi.server-url:http://localhost:9000}")
  private String serverUrl;

  @Value("${openapi.server-description:Gateway - Local}")
  private String serverDescription;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title(title).version("v1.0").description(description))
        .addServersItem(new Server().url(serverUrl).description(serverDescription))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .schemaRequirement(
            "bearerAuth",
            new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));
  }
}
