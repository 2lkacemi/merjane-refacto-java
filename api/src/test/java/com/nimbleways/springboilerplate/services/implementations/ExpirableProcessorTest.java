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
class ExpirableProcessorTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProcessor processor;

    @Test
    void should_decrement_when_not_expired_and_in_stock() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 10);
        Product p = new Product(
                null, 0, 3, "EXPIRABLE", "Milk",
                LocalDate.of(2026, 2, 20),
                null, null
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        assertEquals(2, p.getAvailable());
        verify(productRepository, times(1)).save(p);
        verifyNoInteractions(notificationService);
    }

    @Test
    void should_set_available_0_and_notify_when_expired() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 10);
        Product p = new Product(
                null, 0, 3, "EXPIRABLE", "Milk",
                LocalDate.of(2026, 2, 1),
                null, null
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        assertEquals(0, p.getAvailable());
        verify(notificationService, times(1)).sendExpirationNotification("Milk", LocalDate.of(2026, 2, 1));
        verify(productRepository, times(1)).save(p);
    }

    @Test
    void should_set_available_0_and_notify_when_out_of_stock_even_if_not_expired() {
        // GIVEN
        LocalDate today = LocalDate.of(2026, 2, 10);
        Product p = new Product(
                null, 0, 0, "EXPIRABLE", "Milk",
                LocalDate.of(2026, 2, 20),
                null, null
        );
        when(productRepository.save(p)).thenReturn(p);

        // WHEN
        processor.process(p, today);

        // THEN
        assertEquals(0, p.getAvailable());
        verify(notificationService, times(1)).sendExpirationNotification("Milk", LocalDate.of(2026, 2, 20));
        verify(productRepository, times(1)).save(p);
    }
}