package com.teamsolution.inventory.service.customer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.inventory.dto.request.BrandFilterRequest;
import com.teamsolution.inventory.dto.response.BrandDetailResponse;
import com.teamsolution.inventory.dto.response.BrandSummaryResponse;
import com.teamsolution.inventory.entity.Brand;
import com.teamsolution.inventory.enums.BrandStatus;
import com.teamsolution.inventory.exception.ErrorCode;
import com.teamsolution.inventory.mapper.BrandDetailMapper;
import com.teamsolution.inventory.mapper.BrandSummaryMapper;
import com.teamsolution.inventory.repository.BrandRepository;
import com.teamsolution.inventory.service.customer.BrandService;
import com.teamsolution.inventory.specification.BrandSpecification;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

  private final BrandRepository brandRepository;
  private final BrandSummaryMapper brandSummaryMapper;
  private final BrandDetailMapper brandDetailMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<BrandSummaryResponse> getActiveBrands(Pageable pageable, BrandFilterRequest request) {
    Specification<Brand> spec =
        Specification.where(BrandSpecification.notDeleted(false))
            .and(BrandSpecification.hasStatus(BrandStatus.ACTIVE))
            .and(BrandSpecification.hasKeyword(request.getKeyword()));

    Page<Brand> brands = brandRepository.findAll(spec, pageable);

    return brands.map(brandSummaryMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public BrandDetailResponse getActiveBrandById(UUID id) {
    Brand brand =
        brandRepository
            .findByIsDeletedFalseAndStatusAndId(BrandStatus.ACTIVE, id)
            .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
    return brandDetailMapper.toDto(brand);
  }

  @Override
  @Transactional(readOnly = true)
  public Brand getActiveById(UUID brandId) {
    return brandRepository
        .findByIsDeletedFalseAndStatusAndId(BrandStatus.ACTIVE, brandId)
        .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
  }
}
