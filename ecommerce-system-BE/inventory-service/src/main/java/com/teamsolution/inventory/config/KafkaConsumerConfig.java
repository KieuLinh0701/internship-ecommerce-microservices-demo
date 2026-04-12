package com.teamsolution.inventory.config;

import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.core.exception.TemporaryException;
import com.teamsolution.common.kafka.config.properties.KafkaConsumerProperties;
import com.teamsolution.inventory.service.customer.FailedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  private final KafkaConsumerProperties kafkaConsumerProperties;

  @Bean
  public DefaultErrorHandler errorHandler(
      KafkaTemplate<String, Object> kafkaTemplate, FailedEventService failedEventService) {

    // Recoverer — defines what to do when retries are exhausted or a permanent error occurs
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            // Decide which topic to send the failed message to
            // record = original message, ex = exception thrown
            (record, ex) -> {
              failedEventService.saveFailedEvent(record, ex);

              return new TopicPartition(
                  record.topic() + kafkaConsumerProperties.getDlt().getSuffix(),
                  record.partition());
            });

    // Backoff — defines the wait time between retry attempts
    ExponentialBackOff backOff =
        new ExponentialBackOff(
            kafkaConsumerProperties.getRetry().getInitialIntervalMs(),
            kafkaConsumerProperties.getRetry().getMultiplier());
    backOff.setMaxAttempts(
        kafkaConsumerProperties.getRetry().getMaxKafkaRetry()); // maximum of retry attempts

    // Attach Recoverer + BackOff to the ErrorHandler
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

    // Exceptions that should NOT be retried → immediately handled by the Recoverer
    handler.addNotRetryableExceptions(PermanentException.class);

    // Exceptions that CAN be retried → retry according to backOff, then handle with Recoverer if
    // still failing
    handler.addRetryableExceptions(TemporaryException.class);

    handler.setCommitRecovered(true);

    return handler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      ConsumerFactory<String, Object> consumerFactory, DefaultErrorHandler errorHandler) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler);

    factory.getContainerProperties().setDeliveryAttemptHeader(true);

    return factory;
  }
}
