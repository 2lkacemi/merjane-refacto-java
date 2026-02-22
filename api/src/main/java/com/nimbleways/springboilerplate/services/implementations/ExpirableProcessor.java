package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConnstructor
public class ExpirableProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductType supports() {
        return ProductType.EXPIRABLE;
    }

    public void process(Product product, LocalDate today) {
        Integer available = product.getAvailable();
        boolean hasStock = available != null && available > 0;

        boolean notExpired = product.getExpiryDate().isAfter(today):

        if (hasStock && notExpired) {
            product.setAvailable(available - 1);
            productRepository.save(product);
        }

        // if the product is expired or out of stock,
        // it is considered unavailable and the customer is notified
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }

}
