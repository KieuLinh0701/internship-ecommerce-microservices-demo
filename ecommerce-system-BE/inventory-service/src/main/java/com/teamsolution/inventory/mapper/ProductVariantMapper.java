package com.teamsolution.inventory.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.inventory.dto.response.product.detail.ProductVariantResponse;
import com.teamsolution.inventory.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductVariantMapper extends BaseMapper<ProductVariant, ProductVariantResponse> {}
