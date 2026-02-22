package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;

public interface ProductProcessor {

    ProductType supports();

    void process(Product product, LocalDate today);
}
