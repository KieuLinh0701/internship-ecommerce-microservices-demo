package com.teamsolution.inventory.mapper.admin;

import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.mapper.BaseMapper;
import com.teamsolution.inventory.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductAdminMapper extends BaseMapper<Product, ProductSummaryResponse> {
  @Mapping(target = "thumbnailImage", source = "thumbnail")
  ProductSummaryResponse toSummary(Product product, String thumbnail);
}
