package com.teamsolution.admin.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsolution.admin.exception.ErrorCode;
import com.teamsolution.common.core.dto.common.response.ApiResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import com.teamsolution.common.core.exception.AppException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public abstract class BaseInternalClient {

  protected final RestClient restClient;
  protected final ObjectMapper objectMapper;

  protected static final ParameterizedTypeReference<ApiResponse<Void>> VOID_TYPE =
      new ParameterizedTypeReference<>() {};

  protected <T> PageResponse<T> fetchPage(
      String serviceId,
      String path,
      Object filter,
      ParameterizedTypeReference<ApiResponse<PageResponse<T>>> type) {

    var spec = restClient.get().uri(buildUri(serviceId, path, filter));
    return exchange(spec, serviceId, type);
  }

  protected <T> T getAction(
      String serviceId, String path, ParameterizedTypeReference<ApiResponse<T>> type) {

    var spec = restClient.get().uri("http://" + serviceId + path);

    return exchange(spec, serviceId, type);
  }

  protected <T> T postAction(
      String serviceId, String path, Object body, ParameterizedTypeReference<ApiResponse<T>> type) {

    var spec = restClient.post().uri("http://" + serviceId + path);

    return exchange(body != null ? spec.body(body) : spec, serviceId, type);
  }

  protected <T> T postMultipart(
      String serviceId,
      String path,
      MultiValueMap<String, HttpEntity<?>> body,
      ParameterizedTypeReference<ApiResponse<T>> type) {

    var spec =
        restClient
            .post()
            .uri("http://" + serviceId + path)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body);

    return exchange(spec, serviceId, type);
  }

  protected MultiValueMap<String, Object> buildMultipartBody(
      Map<String, Object> data, List<MultipartFile> files) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    if (files != null) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          body.add("files", file.getResource());
        }
      }
    }

    if (data != null) {
      data.forEach(body::add);
    }

    return body;
  }

  protected <T> T putAction(
      String serviceId, String path, Object body, ParameterizedTypeReference<ApiResponse<T>> type) {

    var spec = restClient.put().uri("http://" + serviceId + path);

    return exchange(body != null ? spec.body(body) : spec, serviceId, type);
  }

  protected <T> T patchAction(
      String serviceId, String path, Object body, ParameterizedTypeReference<ApiResponse<T>> type) {

    var spec = restClient.patch().uri("http://" + serviceId + path);

    return exchange(body != null ? spec.body(body) : spec, serviceId, type);
  }

  protected void deleteAction(String serviceId, String path) {

    var spec = restClient.delete().uri("http://" + serviceId + path);

    exchange(spec, serviceId, VOID_TYPE);
  }

  protected URI buildUri(String serviceId, String path, Object filter) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://" + serviceId + path);
    if (filter != null) {
      Map<String, Object> map = objectMapper.convertValue(filter, new TypeReference<>() {});
      map.forEach(
          (k, v) -> {
            if (v != null) builder.queryParam(k, v);
          });
    }
    return builder.build().toUri();
  }

  protected String extractMessage(String responseBody, String fallback) {
    try {
      ApiResponse<?> apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
      return apiResponse.getMessage();
    } catch (Exception e) {
      return fallback;
    }
  }

  private <T> T exchange(
      RestClient.RequestHeadersSpec<?> spec,
      String serviceId,
      ParameterizedTypeReference<ApiResponse<T>> type) {
    return spec.exchange(
        (req, res) -> {
          if (res.getStatusCode().isError()) {
            ApiResponse<Void> errBody = res.bodyTo(VOID_TYPE);
            throw new AppException(
                ErrorCode.UPSTREAM_SERVICE_ERROR,
                serviceId,
                errBody != null ? errBody.getMessage() : res.getStatusCode().toString());
          }
          return res.bodyTo(type).getData();
        });
  }
}
