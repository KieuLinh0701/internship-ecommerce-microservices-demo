package com.teamsolution.search.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.search.document.ProductDocument;
import com.teamsolution.search.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper extends BaseMapper<ProductDocument, ProductResponse> {}
