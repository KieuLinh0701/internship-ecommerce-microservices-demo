package com.teamsolution.cart.enums;

public enum CartItemStatus {
  ACTIVE,
  OUT_OF_STOCK,
  REMOVED,
  CHECKED_OUT;

    public boolean isVisible() {
        return this == ACTIVE || this == OUT_OF_STOCK;
    }
}
