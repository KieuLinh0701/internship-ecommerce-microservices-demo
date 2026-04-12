package com.teamsolution.inventory.mapper.admin;

import com.teamsolution.common.core.dto.admin.inventory.productimage.response.ProductImageResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.inventory.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductImageAdminMapper extends BaseMapper<ProductImage, ProductImageResponse> {}
