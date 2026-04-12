package com.teamsolution.notification.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.notification.dto.response.NotificationResponse;
import com.teamsolution.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper extends BaseMapper<Notification, NotificationResponse> {

  @Override
  @Mapping(target = "isRead", expression = "java(entity.isRead())")
  NotificationResponse toDto(Notification entity);
}
