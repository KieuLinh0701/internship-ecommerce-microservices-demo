package com.teamsolution.admin.dto.response;

import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.dto.common.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponse {
  private String serviceId;
  private PageResponse<FailedEventResponse> page;
  private boolean ok;
  private String error;

  public static ServiceResponse ok(String serviceId, PageResponse<FailedEventResponse> page) {
    return new ServiceResponse(serviceId, page, true, null);
  }

  public static ServiceResponse error(String serviceId, String msg) {
    return new ServiceResponse(serviceId, null, false, msg);
  }
}
