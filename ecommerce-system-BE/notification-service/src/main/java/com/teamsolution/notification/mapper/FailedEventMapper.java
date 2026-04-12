package com.teamsolution.notification.mapper;

import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.notification.entity.FailedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FailedEventMapper extends BaseMapper<FailedEvent, FailedEventResponse> {}
