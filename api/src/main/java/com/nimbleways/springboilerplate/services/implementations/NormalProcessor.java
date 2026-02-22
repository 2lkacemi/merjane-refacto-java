package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConnstructor
public class NormalProcessor implements ProductProcessor {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductType supports() {
        return ProductType.NORMAL;
    }

    public void process(Product product, LocalDate today) {
        Integer available = product.getAvailable();
        if (available != null && available > 0) {
            product.setAvailable(available - 1);
            productRepository.save(product);
        }
        Integer leadTime = product.getLeadTime();
        if (leadTime != null && leadTime > 0) {
            notificationService.sendDelayNotification(leadTime, product.getName());
            productRepository.save(product);
        }
    }


}
