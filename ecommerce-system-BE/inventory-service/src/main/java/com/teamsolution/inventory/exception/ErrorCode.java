package com.teamsolution.inventory.exception;

import com.teamsolution.common.core.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

  // Brand
  BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "Brand not found"),

  // Category
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),

  // Product
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
  PRODUCT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "Product has already been deleted"),
  PRODUCT_NOT_DELETED(HttpStatus.BAD_REQUEST, "Product is not deleted"),
  PRODUCT_SLUG_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "The product slug already exists"),

  // Product Variant
  PRODUCT_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Variant not found"),
  PRODUCT_VARIANT_SKU_ALREADY_EXISTS(
      HttpStatus.BAD_REQUEST, "The product variant sku already exists"),

  // Product Variant Inventory
  PRODUCT_VARIANT_INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Product Variant Inventory not found"),
  UNHANDLED_ORDER_STATUS(HttpStatus.BAD_REQUEST, "Unhandled order status: %s for order: %s"),
  VARIANT_NOT_FOUND_OR_STOCK_MISMATCH(
      HttpStatus.BAD_REQUEST, "Failed to restore confirmed stock for variant: %s"),
  CONFIRM_STOCK_FAILED(
      HttpStatus.BAD_REQUEST, "Variant %s not found or reserved quantity insufficient to confirm"),
  RESTORE_CONFIRMED_STOCK_FAILED(
      HttpStatus.BAD_REQUEST,
      "Variant %s not found or sold quantity insufficient to restore confirmed stock"),
  INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for variant: %s"),

  // Product Image
  PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image not found"),
  PRODUCT_IMAGE_INVALID_STATUS(
      HttpStatus.BAD_REQUEST, "Image must be in TEMP status to attach product"),
  PRODUCT_IMAGE_ALREADY_ATTACHED(HttpStatus.BAD_REQUEST, "Image already belongs to a product"),
  PRODUCT_IMAGE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "Image already deleted"),
  PRODUCT_IMAGE_MAX_FILES_EXCEEDED(HttpStatus.BAD_REQUEST, "Maximum 5 images are allowed"),
  PRODUCT_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload image failed for file: %s"),

  // Attribute Value
  ATTRIBUTE_VALUE_INVALID(
      HttpStatus.BAD_REQUEST, "Attribute value is invalid, deleted or inactive"),

  // Attribute
  ATTRIBUTE_INVALID(HttpStatus.BAD_REQUEST, "Attribute is invalid or inactive"),
  ATTRIBUTE_DUPLICATE_IN_VARIANT(
      HttpStatus.BAD_REQUEST, "Each attribute can only have one value per variant");

  private final HttpStatus httpStatus;
  private final String message;
}
