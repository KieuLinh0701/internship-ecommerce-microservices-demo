package com.teamsolution.grpc.client.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.teamsolution.common.core.exception.TemporaryException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServiceDiscovery {

  private final EurekaClient eurekaClient;
  private static final String GRPC_PORT = "grpcPort";

  public String getGrpcTarget(String serviceName) {

    // Get server info from Eureka registry
    Application application = eurekaClient.getApplication(serviceName.toUpperCase());

    // Service not found in Eureka
    if (application == null) {
      log.error("GRPC_SERVICE_NOT_FOUND server={}", serviceName);
      throw new TemporaryException(CommonErrorCode.GRPC_SERVICE_NOT_FOUND);
    }

    // No running instances available
    List<InstanceInfo> instances = application.getInstances();

    if (instances == null || instances.isEmpty()) {
      log.error("GRPC_NO_INSTANCE_AVAILABLE server={}", serviceName);
      throw new TemporaryException(CommonErrorCode.GRPC_NO_INSTANCE_AVAILABLE);
    }

    // Shuffle for simple load balancing
    Collections.shuffle(instances);

    // Retry across instances
    for (InstanceInfo instance : instances) {

      try {
        String grpcPort = instance.getMetadata().get(GRPC_PORT);

        if (grpcPort == null) {
          log.warn(
              "GRPC_METADATA_MISSING server={} instanceId={}",
              serviceName,
              instance.getInstanceId());
          continue;
        }

        String target = instance.getIPAddr() + ":" + grpcPort;

        log.debug(
            "GRPC_TARGET_SELECTED server={} instanceId={} target={}",
            serviceName,
            instance.getInstanceId(),
            target);

        return target;
      } catch (Exception e) {
        log.warn(
            "GRPC_INSTANCE_FAILED server={} instanceId={}", serviceName, instance.getInstanceId());
      }
    }

    // All instances failed
    throw new TemporaryException(CommonErrorCode.GRPC_NO_INSTANCE_AVAILABLE);
  }
}
