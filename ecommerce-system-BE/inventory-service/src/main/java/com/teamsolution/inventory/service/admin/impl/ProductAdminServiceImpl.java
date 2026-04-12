package com.teamsolution.inventory.service.admin.impl;

import com.teamsolution.common.core.dto.admin.inventory.product.request.FilterProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.CreateProductRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.ImageRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.request.create.VariantRequest;
import com.teamsolution.common.core.dto.admin.inventory.product.response.ProductSummaryResponse;
import com.teamsolution.common.core.enums.common.StatusChangeReason;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.inventory.entity.AttributeValue;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.entity.Category;
import com.teamsolution.inventory.entity.Product;
import com.teamsolution.inventory.entity.ProductImage;
import com.teamsolution.inventory.entity.ProductVariant;
import com.teamsolution.inventory.entity.ProductVariantAttributeValue;
import com.teamsolution.inventory.entity.ProductVariantInventory;
import com.teamsolution.inventory.enums.AttributeStatus;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.enums.ProductImageStatus;
import com.teamsolution.inventory.enums.ProductVariantStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.kafka.producer.ProductProducer;
import com.teamsolution.inventory.mapper.admin.ProductAdminMapper;
import com.teamsolution.inventory.repository.ProductRepository;
import com.teamsolution.inventory.service.admin.ProductAdminService;
import com.teamsolution.inventory.service.customer.BrandService;
import com.teamsolution.inventory.service.customer.CategoryService;
import com.teamsolution.inventory.service.internal.AttributeValueInternalService;
import com.teamsolution.inventory.service.internal.ProductImageInternalService;
import com.teamsolution.inventory.service.internal.ProductVariantInternalService;
import com.teamsolution.inventory.specification.ProductSpecification;
import com.teamsolution.inventory.utils.ProductStatusPolicy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

  private final ProductRepository productRepository;
  private final ProductImageInternalService productImageInternalService;
  private final BrandService brandService;
  private final CategoryService categoryService;
  private final ProductVariantInternalService productVariantInternalService;
  private final AttributeValueInternalService attributeValueInternalService;
  private final ProductAdminMapper productAdminMapper;
  private final ProductProducer productProducer;

  @Override
  public Page<ProductSummaryResponse> getAll(
      FilterProductRequest filterRequest, Pageable pageable) {

    Specification<Product> specification = buildProductSpecification(filterRequest);

    Page<Product> products = productRepository.findAll(specification, pageable);

    List<UUID> productIds = extractProductIds(products);

    List<ProductImage> allImages =
        productImageInternalService.findValidImagesByProductIds(productIds);

    Map<UUID, List<ProductImage>> imageMap = groupImages(allImages);

    return products.map(product -> mapProductSummary(product, imageMap));
  }

  @Override
  @Transactional
  public void delete(UUID productId, UUID adminAccountId) {
    Product product = findByIdIncludingDeleted(productId);

    if (product.getIsDeleted() && product.getStatus() == ProductStatus.DELETED) {
      throw new AppException(ErrorCode.PRODUCT_ALREADY_DELETED);
    }

    ProductStatus newStatus = ProductStatus.DELETED;

    product.setStatus(newStatus);
    product.setDeletedAt(LocalDateTime.now());
    product.setIsDeleted(true);
    product.setDeletedBy(adminAccountId);

    productRepository.save(product);

    productVariantInternalService.updateStatus(
        productId,
        List.of(
            ProductVariantStatus.ACTIVE, ProductVariantStatus.INACTIVE, ProductVariantStatus.DRAFT),
        List.of(),
        newStatus,
        StatusChangeReason.CASCADE,
        adminAccountId,
        InventoryEventType.DELETE_PRODUCT);

    productImageInternalService.updateStatus(
        productId,
        List.of(
            ProductImageStatus.ACTIVE,
            ProductImageStatus.INACTIVE,
            ProductImageStatus.DRAFT,
            ProductImageStatus.TEMP),
        List.of(),
        newStatus,
        StatusChangeReason.CASCADE,
        adminAccountId,
        InventoryEventType.DELETE_PRODUCT);

    productProducer.changeProductStatus(
        product.getId(), product.getStatus(), InventoryEventType.DELETE_PRODUCT);
  }

  @Override
  public void restore(UUID productId, UUID adminAccountId) {
    Product product = findByIdIncludingDeleted(productId);

    if (!product.getIsDeleted() && product.getStatus() != ProductStatus.DELETED) {
      throw new AppException(ErrorCode.PRODUCT_NOT_DELETED);
    }

    ProductStatus newStatus = ProductStatus.INACTIVE;
    product.setStatus(newStatus);
    product.setIsDeleted(false);
    product.setDeletedAt(null);
    product.setDeletedBy(null);

    productRepository.save(product);

    productVariantInternalService.updateStatus(
        productId,
        List.of(ProductVariantStatus.DELETED),
        List.of(StatusChangeReason.CASCADE),
        newStatus,
        StatusChangeReason.CASCADE,
        adminAccountId,
        InventoryEventType.RESTORE_PRODUCT);
    productImageInternalService.updateStatus(
        productId,
        List.of(ProductImageStatus.DELETED),
        List.of(StatusChangeReason.CASCADE),
        newStatus,
        StatusChangeReason.CASCADE,
        adminAccountId,
        InventoryEventType.RESTORE_PRODUCT);

    productProducer.changeProductStatus(
        product.getId(), product.getStatus(), InventoryEventType.RESTORE_PRODUCT);
  }

  @Override
  @Transactional
  public ProductSummaryResponse create(CreateProductRequest request) {

    if (productRepository.existsBySlug(request.slug())) {
      throw new AppException(ErrorCode.PRODUCT_SLUG_ALREADY_EXISTS);
    }

    Product product = buildProduct(request);

    List<ProductVariant> variants = handleVariants(request, product);
    List<ProductImage> images = handleImages(request, product);

    updateProductPriceRangeAndThumbnail(product, variants, images);

    productRepository.saveAndFlush(product);

    productVariantInternalService.saveAll(variants);
    productImageInternalService.saveAll(images);

    String thumbnail = resolveThumbnail(images);

    productProducer.changeProduct(
        product,
        product.getCategory(),
        product.getBrand(),
        thumbnail,
        InventoryEventType.CREATE_PRODUCT);

    return productAdminMapper.toSummary(product, thumbnail);
  }

  private Specification<Product> buildProductSpecification(FilterProductRequest request) {
    return Specification.where(ProductSpecification.isDeleted(request.getIsDeleted()))
        .and(ProductSpecification.hasStatus(request.getStatus()))
        .and(ProductSpecification.hasBrandId(request.getBrandId()))
        .and(ProductSpecification.hasCategoryId(request.getCategoryId()))
        .and(ProductSpecification.hasKeyword(request.getKeyword()))
        .and(ProductSpecification.hasPriceRange(request.getMinPrice(), request.getMaxPrice()));
  }

  private List<ProductImage> handleImages(CreateProductRequest request, Product product) {
    if (request.images() == null || request.images().isEmpty()) {
      return List.of();
    }

    List<UUID> imageIds = extractImageIds(request.images());

    List<ProductImage> images = productImageInternalService.findValidTempImages(imageIds);

    if (images.size() != imageIds.size()) {
      throw new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
    }

    Map<UUID, Integer> sortMap = buildSortMap(request.images());

    images.forEach(img -> applyImageMapping(img, product, sortMap));

    return images;
  }

  private List<UUID> extractImageIds(List<ImageRequest> images) {
    return images.stream().map(ImageRequest::id).toList();
  }

  private Map<UUID, Integer> buildSortMap(List<ImageRequest> images) {
    return images.stream()
        .collect(
            Collectors.toMap(ImageRequest::id, r -> r.sortOrder() == null ? 0 : r.sortOrder()));
  }

  private void applyImageMapping(ProductImage img, Product product, Map<UUID, Integer> sortMap) {
    img.setProduct(product);
    img.setStatus(ProductStatusPolicy.mapImageStatus(product.getStatus()));
    img.setSortOrder(sortMap.get(img.getId()));
  }

  private List<ProductVariant> handleVariants(CreateProductRequest request, Product product) {
    return request.variants().stream().map(v -> createVariant(v, product)).toList();
  }

  private Product buildProduct(CreateProductRequest request) {

    Category category = categoryService.getActiveById(request.categoryId());
    Brand brand = brandService.getActiveById(request.brandId());

    return Product.builder()
        .name(request.name())
        .slug(request.slug())
        .description(request.description())
        .category(category)
        .brand(brand)
        .status(request.status())
        .build();
  }

  private void updateProductPriceRangeAndThumbnail(
      Product product, List<ProductVariant> variants, List<ProductImage> images) {

    if (variants == null || variants.isEmpty()) {
      product.setMinPrice(0L);
      product.setMaxPrice(0L);
      product.setThumbnailUrl(null);
      return;
    }

    Long min = variants.stream().map(ProductVariant::getPrice).min(Long::compareTo).orElse(0L);
    Long max = variants.stream().map(ProductVariant::getPrice).max(Long::compareTo).orElse(0L);

    product.setMinPrice(min);
    product.setMaxPrice(max);
    product.setThumbnailUrl(resolveThumbnail(images));
  }

  private ProductVariant createVariant(VariantRequest request, Product product) {

    if (productVariantInternalService.existsBySku(request.sku())) {
      throw new AppException(ErrorCode.PRODUCT_VARIANT_SKU_ALREADY_EXISTS);
    }

    ProductVariant variant =
        ProductVariant.builder()
            .product(product)
            .sku(request.sku())
            .price(request.price())
            .status(ProductStatusPolicy.mapVariantStatus(product.getStatus()))
            .compareAtPrice(request.compareAtPrice())
            .imageUrl(request.imageUrl())
            .build();

    if (request.attributeValueIds() != null) {

      List<AttributeValue> values =
          attributeValueInternalService.findValidActiveByIds(request.attributeValueIds());

      if (values.size() != request.attributeValueIds().size()) {
        throw new AppException(ErrorCode.ATTRIBUTE_VALUE_INVALID);
      }

      values.forEach(
          v -> {
            if (v.getAttribute().getStatus() != AttributeStatus.ACTIVE) {
              throw new AppException(ErrorCode.ATTRIBUTE_INVALID);
            }
          });

      Map<UUID, Long> attributeCount =
          values.stream()
              .collect(Collectors.groupingBy(v -> v.getAttribute().getId(), Collectors.counting()));

      boolean hasDuplicate = attributeCount.values().stream().anyMatch(count -> count > 1);

      if (hasDuplicate) {
        throw new AppException(ErrorCode.ATTRIBUTE_DUPLICATE_IN_VARIANT);
      }

      List<ProductVariantAttributeValue> mappings = new ArrayList<>();

      for (AttributeValue val : values) {
        mappings.add(
            ProductVariantAttributeValue.builder().variant(variant).attributeValue(val).build());
      }

      variant.setAttributeValues(mappings);
    }

    variant.setInventory(buildInventory(request, variant, product.getStatus()));

    return variant;
  }

  private ProductVariantInventory buildInventory(
      VariantRequest request, ProductVariant variant, ProductStatus productStatus) {

    if (request.inventory() == null) {
      return ProductVariantInventory.builder()
          .variant(variant)
          .quantity(0)
          .status(ProductStatusPolicy.mapInventoryStatus(productStatus))
          .build();
    }

    return ProductVariantInventory.builder()
        .variant(variant)
        .quantity(request.inventory().quantity())
        .lowStockThreshold(request.inventory().lowStockThreshold())
        .manufactureDate(request.inventory().manufactureDate())
        .expiryDate(request.inventory().expiryDate())
        .status(ProductStatusPolicy.mapInventoryStatus(productStatus))
        .build();
  }

  private Product findByIdIncludingDeleted(UUID id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  private String resolveThumbnail(List<ProductImage> images) {

    if (images == null || images.isEmpty()) return null;

    return images.stream()
        .filter(img -> img.getSortOrder() == 0)
        .map(ProductImage::getImageUrl)
        .findFirst()
        .orElse(images.get(0).getImageUrl());
  }

  private List<UUID> extractProductIds(Page<Product> products) {
    return products.getContent().stream().map(Product::getId).toList();
  }

  private Map<UUID, List<ProductImage>> groupImages(List<ProductImage> images) {
    return images.stream().collect(Collectors.groupingBy(img -> img.getProduct().getId()));
  }

  private ProductSummaryResponse mapProductSummary(
      Product product, Map<UUID, List<ProductImage>> imageMap) {
    return productAdminMapper.toSummary(product, resolveThumbnail(imageMap.get(product.getId())));
  }

  private String generateSlug(String name) {
    return name.toLowerCase().trim().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");
  }
}
