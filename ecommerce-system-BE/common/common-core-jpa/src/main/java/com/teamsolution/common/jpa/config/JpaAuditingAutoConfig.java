package com.teamsolution.common.jpa.config;

import com.teamsolution.common.core.security.SecurityUtils;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnClass(JpaRepository.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingAutoConfig {

  @Bean
  public AuditorAware<UUID> auditorAwareImpl() {
    return () -> {
      try {
        return Optional.ofNullable(SecurityUtils.getCurrentAccountId());
      } catch (Exception e) {
        return Optional.empty();
      }
    };
  }
}
