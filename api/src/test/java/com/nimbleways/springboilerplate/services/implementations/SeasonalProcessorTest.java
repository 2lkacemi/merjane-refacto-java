package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeasonalProcessorTest {

    @Mock private ProductRepository productRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks private SeasonalProcessor processor;

    @Test
    void should_decrement_stock_when_in_season_and_available_gt_0() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 10);
        Product p = new Product(
                null,
                10,
                5,
                "SEASONAL",
                "Grapes",
                null,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 3, 1)
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        assertEquals(4, p.getAvailable());
        verify(productRepository, times(1)).save(p);
        verifyNoInteractions(notificationService);
    }

    @Test
    void should_send_out_of_stock_when_before_season_start() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 1);
        Product p = new Product(
                null,
                10,
                0,
                "SEASONAL",
                "Grapes",
                null,
                LocalDate.of(2026, 2, 10),
                LocalDate.of(2026, 3, 10)
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        verify(notificationService, times(1)).sendOutOfStockNotification("Grapes");
        verify(productRepository, times(1)).save(p);
    }

    @Test
    void should_send_out_of_stock_and_set_available_0_when_delay_exceeds_season_end() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 20);
        Product p = new Product(
                null,
                20, // today + 20 > seasonEnd => out of stock
                0,
                "SEASONAL",
                "Grapes",
                null,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 3, 1)
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        assertEquals(0, p.getAvailable());
        verify(notificationService, times(1)).sendOutOfStockNotification("Grapes");
        verify(productRepository, times(1)).save(p);
    }

    @Test
    void should_send_delay_when_delay_does_not_exceed_season_end_and_season_started() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 10);
        Product p = new Product(
                null,
                5, // today + 5 <= seasonEnd => delay ok
                0,
                "SEASONAL",
                "Grapes",
                null,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 3, 1)
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        verify(notificationService, times(1)).sendDelayNotification(5, "Grapes");
        verify(productRepository, times(1)).save(p);
    }
}