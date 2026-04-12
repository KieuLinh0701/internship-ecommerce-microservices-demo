package com.teamsolution.search.repository;

import com.teamsolution.search.document.ProductDocument;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, UUID> {}
