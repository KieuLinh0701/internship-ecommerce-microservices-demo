package com.teamsolution.inventory.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.inventory.dto.response.product.detail.ProductImageResponse;
import com.teamsolution.inventory.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductImageMapper extends BaseMapper<ProductImage, ProductImageResponse> {}
