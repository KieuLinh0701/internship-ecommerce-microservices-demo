package com.teamsolution.inventory.service.customer.impl;

import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.inventory.dto.response.product.detail.AttributeValueResponse;
import com.teamsolution.inventory.dto.response.product.detail.AttributeWithValuesResponse;
import com.teamsolution.inventory.dto.response.product.detail.BrandResponse;
import com.teamsolution.inventory.dto.response.product.detail.CategoryResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductDetailResponse;
import com.teamsolution.inventory.dto.response.product.detail.ProductImageResponse;
import com.teamsolution.inventory.entity.AttributeValue;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.entity.Product;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.enums.BrandStatus;
import com.teamsolution.inventory.enums.CategoryStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.mapper.ProductDetailBrandMapper;
import com.teamsolution.inventory.mapper.ProductDetailCategoryMapper;
import com.teamsolution.inventory.mapper.ProductVariantMapper;
import com.teamsolution.inventory.repository.ProductRepository;
import com.teamsolution.inventory.service.customer.ProductService;
import com.teamsolution.inventory.service.internal.ProductImageInternalService;
import com.teamsolution.inventory.service.internal.ProductVariantInternalService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductImageInternalService productImageInternalService;
  private final ProductVariantInternalService productVariantInternalService;
  private final ProductVariantMapper productVariantMapper;
  private final ProductDetailBrandMapper productDetailBrandMapper;
  private final ProductDetailCategoryMapper productDetailCategoryMapper;

  private static final Set<BrandStatus> ACTIVE_BRANDS = Set.of(BrandStatus.ACTIVE);
  private static final Set<CategoryStatus> ACTIVE_CATEGORIES = Set.of(CategoryStatus.ACTIVE);

  @Override
  @Transactional(readOnly = true)
  public ProductDetailResponse getProductById(UUID id) {
    Product product =
        productRepository
            .findDetailProduct(id, List.of(ProductStatus.ACTIVE, ProductStatus.INACTIVE))
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    List<ProductImageResponse> images = productImageInternalService.getByProductId(id);

    List<ProductVariant> variants = productVariantInternalService.getByProductId(id);

    return buildProductDetail(product, images, variants);
  }

  //  @Override
  //  @Transactional(readOnly = true)
  //  public ProductVariantDto getVariantById(UUID productId, UUID variantId) {
  //    productRepository
  //        .findByIdAndIsDeleteFalseAndStatus(productId, ProductStatus.ACTIVE)
  //        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
  //
  //    // Get Variant
  //    ProductVariant variant = productVariantInternalService.findByIdAndProductId(productId,
  // variantId);
  //
  //    // Check valid variant
  //    if (!isVariantValid(variant)) {
  //      throw new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND);
  //    }
  //
  //    return productVariantMapper.toDto(variant);
  //  }

  private ProductDetailResponse buildProductDetail(
      Product product, List<ProductImageResponse> images, List<ProductVariant> variants) {
    // Check Brand: Brand is active and not deleted
    BrandResponse brand = getActiveBrand(product.getBrand());

    CategoryResponse category = getActiveCategory(product.getCategory());

    // Check product variants: Variant is not deleted and its attribute value is not deleted
    List<ProductVariant> validVariants = getValidProductVariant(variants);

    // Build attributes from valid variants
    List<AttributeWithValuesResponse> attributes = buildAttributes(validVariants);

    return ProductDetailResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .slug(product.getSlug())
        .description(product.getDescription())
        .minPrice(product.getMinPrice())
        .maxPrice(product.getMaxPrice())
        .status(product.getStatus())
        .category(category)
        .brand(brand)
        .images(images)
        .variants(productVariantMapper.toDtoList(validVariants))
        .attributes(attributes)
        .build();
  }

  // Build attributes
  private List<AttributeWithValuesResponse> buildAttributes(List<ProductVariant> variants) {

    Map<UUID, AttributeWithValuesResponse> attributeMap = new LinkedHashMap<>();

    Map<UUID, Integer> attrSortOrderMap = new HashMap<>();
    Map<UUID, Integer> valueSortOrderMap = new HashMap<>();

    variants.forEach(
        v ->
            v.getAttributeValues()
                .forEach(
                    pvav -> {
                      var av = pvav.getAttributeValue();
                      var attr = av.getAttribute();

                      UUID attrId = attr.getId();

                      attributeMap.computeIfAbsent(
                          attrId,
                          k ->
                              AttributeWithValuesResponse.builder()
                                  .id(attrId)
                                  .name(attr.getName())
                                  .values(new ArrayList<>())
                                  .build());

                      var values = attributeMap.get(attrId).getValues();

                      if (values.stream().noneMatch(x -> x.getId().equals(av.getId()))) {
                        values.add(
                            AttributeValueResponse.builder()
                                .id(av.getId())
                                .value(av.getValue())
                                .build());
                      }

                      attrSortOrderMap.putIfAbsent(attrId, attr.getSortOrder());
                      valueSortOrderMap.putIfAbsent(av.getId(), av.getSortOrder());
                    }));

    return attributeMap.values().stream()
        .sorted(Comparator.comparing(a -> attrSortOrderMap.getOrDefault(a.getId(), 0)))
        .peek(
            a ->
                a.getValues()
                    .sort(Comparator.comparing(v -> valueSortOrderMap.getOrDefault(v.getId(), 0))))
        .toList();
  }

  private BrandResponse getActiveBrand(Brand brand) {
    BrandResponse brandRes = null;
    if (brand != null && !brand.getIsDeleted() && ACTIVE_BRANDS.contains(brand.getStatus())) {
      brandRes = productDetailBrandMapper.toDto(brand);
    }
    return brandRes;
  }

  private CategoryResponse getActiveCategory(Category category) {
    CategoryResponse categoryRes = null;
    if (category != null
        && !category.getIsDeleted()
        && ACTIVE_CATEGORIES.contains(category.getStatus())) {
      categoryRes = productDetailCategoryMapper.toDto(category);
    }
    return categoryRes;
  }

  private List<ProductVariant> getValidProductVariant(List<ProductVariant> variants) {
    if (variants == null) return List.of();

    return variants.stream()
        .filter(v -> Boolean.FALSE.equals(v.getIsDeleted()))
        .filter(this::isVariantValid)
        .toList();
  }

  // A variant is valid only if ALL of its attribute values are valid
  private boolean isVariantValid(ProductVariant variant) {
    return variant.getAttributeValues() != null
        && variant.getAttributeValues().stream()
            .allMatch(
                pvav -> {
                  AttributeValue av = pvav.getAttributeValue();
                  return av != null
                      && !Boolean.TRUE.equals(av.getIsDeleted())
                      && av.getAttribute() != null
                      && !Boolean.TRUE.equals(av.getAttribute().getIsDeleted());
                });
  }
}
