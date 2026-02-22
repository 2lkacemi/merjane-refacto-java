package com.nimbleways.springboilerplate.services.implementations;

public enum ProductType {
    NORMAL,
    SEASONAL,
    EXPIRABLE;

    public static ProductType from(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Product type is null");
        }
        return ProductType.valueOf(raw.trim().toUpperCase());
    }
}
