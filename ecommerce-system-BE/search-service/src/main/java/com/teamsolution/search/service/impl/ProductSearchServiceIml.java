package com.teamsolution.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import com.teamsolution.common.core.enums.inventory.ProductStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.search.constant.ProductESField;
import com.teamsolution.search.document.ProductDocument;
import com.teamsolution.search.dto.request.ProductFilterRequest;
import com.teamsolution.search.dto.response.ProductResponse;
import com.teamsolution.search.exception.ErrorCode;
import com.teamsolution.search.mapper.ProductMapper;
import com.teamsolution.search.repository.ProductSearchRepository;
import com.teamsolution.search.service.ProductSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceIml implements ProductSearchService {

  private final ElasticsearchOperations elasticsearchOperations;
  private final ProductSearchRepository productSearchRepository;
  private final ProductMapper productMapper;

  @Override
  public Page<ProductResponse> search(ProductFilterRequest request, Pageable pageable) {

    BoolQuery.Builder boolQuery = new BoolQuery.Builder();

    boolQuery.filter(
        f -> f.term(t -> t.field(ProductESField.STATUS).value(ProductStatus.ACTIVE.name())));

    if (hasText(request.getKeyword())) {
      boolQuery.must(
          m ->
              m.multiMatch(
                  mm ->
                      mm.fields(
                              ProductESField.NAME + "^3",
                              ProductESField.DESCRIPTION,
                              ProductESField.CATEGORY_NAME,
                              ProductESField.BRAND_NAME)
                          .query(request.getKeyword())
                          .fuzziness("AUTO")));
    }

    if (request.getBrandId() != null) {
      boolQuery.filter(
          f ->
              f.term(t -> t.field(ProductESField.BRAND_ID).value(request.getBrandId().toString())));
    }

    if (request.getCategoryId() != null) {
      boolQuery.filter(
          f ->
              f.term(
                  t ->
                      t.field(ProductESField.CATEGORY_ID)
                          .value(request.getCategoryId().toString())));
    }

    if (request.getMinPrice() != null || request.getMaxPrice() != null) {

      boolQuery.filter(
          f ->
              f.bool(
                  b -> {
                    if (request.getMinPrice() != null) {
                      b.must(
                          m ->
                              m.range(
                                  r ->
                                      r.field(ProductESField.MAX_PRICE)
                                          .gte(JsonData.of(request.getMinPrice()))));
                    }

                    if (request.getMaxPrice() != null) {
                      b.must(
                          m ->
                              m.range(
                                  r ->
                                      r.field(ProductESField.MIN_PRICE)
                                          .lte(JsonData.of(request.getMaxPrice()))));
                    }

                    return b;
                  }));
    }

    Sort sort = buildSort(request.getSortBy(), request.getKeyword());

    Pageable finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

    NativeQuery query =
        NativeQuery.builder()
            .withQuery(boolQuery.build()._toQuery())
            .withPageable(finalPageable)
            .build();

    SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);

    List<ProductResponse> content =
        productMapper.toDtoList(hits.getSearchHits().stream().map(SearchHit::getContent).toList());

    return new PageImpl<>(content, finalPageable, hits.getTotalHits());
  }

  @Override
  public void sync(ProductChangedEvent event) {

    ProductDocument doc =
        ProductDocument.builder()
            .id(event.getProductId())
            .name(event.getName())
            .description(event.getDescription())
            .categoryId(event.getCategoryId())
            .categoryName(event.getCategoryName())
            .brandId(event.getBrandId())
            .brandName(event.getBrandName())
            .minPrice(event.getMinPrice())
            .maxPrice(event.getMaxPrice())
            .avgPrice((event.getMinPrice() + event.getMaxPrice()) / 2)
            .thumbnail(event.getThumbnail())
            .status(event.getStatus())
            .build();

    productSearchRepository.save(doc);
  }

  @Override
  public void updateStatus(ProductStatusChangedEvent event) {
    ProductDocument doc =
        productSearchRepository
            .findById(UuidUtils.parse(event.getProductId()))
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    doc.setStatus(event.getStatus());

    productSearchRepository.save(doc);
  }

  private Sort buildSort(String sortBy, String keyword) {
    if (sortBy != null && !sortBy.isBlank()) {
      return switch (sortBy) {
        case "price_asc" -> Sort.by(Sort.Direction.ASC, ProductESField.AVG_PRICE);
        case "price_desc" -> Sort.by(Sort.Direction.DESC, ProductESField.AVG_PRICE);
        case "best_selling" -> Sort.by(Sort.Direction.DESC, ProductESField.SOLD_COUNT);
        default -> Sort.by(Sort.Direction.DESC, ProductESField.CREATED_AT);
      };
    }

    if (!hasText(keyword)) {
      return Sort.by(Sort.Direction.DESC, ProductESField.CREATED_AT);
    }

    return Sort.unsorted();
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
