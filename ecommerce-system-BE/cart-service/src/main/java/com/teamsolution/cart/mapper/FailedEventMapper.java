package com.teamsolution.cart.mapper;

import com.teamsolution.cart.entity.FailedEvent;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FailedEventMapper
        extends BaseMapper<FailedEvent, FailedEventResponse> {}
