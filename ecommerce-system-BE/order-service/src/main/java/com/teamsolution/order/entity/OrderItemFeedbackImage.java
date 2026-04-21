package com.teamsolution.order.entity;

import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.order.enums.OrderItemFeedbackImageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "order_item_feedback_image")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class OrderItemFeedbackImage {

  @Id
  @Column(columnDefinition = "uuid")
  @EqualsAndHashCode.Include
  @Builder.Default
  private UUID id = UuidUtils.generate();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feedback_id", nullable = false)
  private OrderItemFeedback feedback;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private OrderItemFeedbackImageStatus status = OrderItemFeedbackImageStatus.TEMP;

  @Column(nullable = false)
  private String publicId;

  @Column(name = "sort_order", nullable = false)
  @Builder.Default
  private Integer sortOrder = 0;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private UUID createdBy;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = false;

  @Version
  @Column(name = "version")
  private Long version;
}
