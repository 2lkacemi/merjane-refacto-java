package com.nimbleways.springboilerplate.services.implementations;


import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConnstructor
public class SeasonalProcessor implements ProductProcessor {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductType supports() {
        return ProductType.SEASONAL;
    }

    public void process(Product product, LocalDate today) {
        boolean inSeason =
                today.isAfter(product.getSeasonStartDate()) && today.isBefore(product.getSeasonEndDate());

        Integer available = product.getAvailable();

        if (inSeason && available != null && available > 0) {
            product.setAvailable(available - 1);
            productRepository.save(product);
        }

        //case SEASONAL
        Integer leadTime = product.getLeadTime() == null ? 0 : product.getLeadTime();

        //If lead time exceeds end of season => unavailble + notif
        if (today.plusDays(leadTime).isAfter(product.getSeasonEndDate)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        }

        //If the season has not yet started => UNAVAILABLE + NOTIF
        if (product.getSeasonStartDate().isAfter(today)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        }

        //else delay ok
        notificationService.sendDelayNotification(leadTime, product.getName());
        productRepository.save(product);
    }
}
