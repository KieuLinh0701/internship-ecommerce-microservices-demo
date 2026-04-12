package com.teamsolution.search.document;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument {

  @Id private String id;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String name;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Keyword)
  private String categoryId;

  @Field(type = FieldType.Keyword)
  private String brandId;

  @Field(type = FieldType.Keyword)
  private String categoryName;

  @Field(type = FieldType.Keyword)
  private String brandName;

  @Field(type = FieldType.Long)
  private Long minPrice;

  @Field(type = FieldType.Long)
  private Long maxPrice;

  @Field(type = FieldType.Long)
  private Long avgPrice;

  @Field(type = FieldType.Long)
  private Long soldCount;

  @Field(type = FieldType.Keyword)
  private String status;

  @Field(type = FieldType.Keyword)
  private String thumbnail;

  @Field(type = FieldType.Date)
  private LocalDate createdAt;
}
