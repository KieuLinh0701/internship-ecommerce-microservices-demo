package com.teamsolution.common.kafka.config;

import com.teamsolution.common.kafka.config.properties.KafkaConsumerProperties;
import com.teamsolution.common.kafka.config.properties.OutboxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.teamsolution.common.kafka")
@EnableConfigurationProperties({KafkaConsumerProperties.class, OutboxProperties.class})
public class CommonKafkaAutoConfiguration {
}
