package com.teamsolution.inventory.mapper;

import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.inventory.dto.response.product.detail.BrandResponse;
import com.teamsolution.inventory.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductDetailBrandMapper extends BaseMapper<Brand, BrandResponse> {}
