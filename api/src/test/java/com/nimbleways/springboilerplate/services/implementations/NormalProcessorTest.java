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
class NormalProcessorTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private NormalProcessor normalProcessor;

    @Test
    void should_send_delay_notification_when_out_of_stock_and_lead_time_positive() {
        // GIVEN
        Product product = new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        normalProcessor.process(product, LocalDate.now());

        // THEN
        // stock doesnt change
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());

        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1))
                .sendDelayNotification(product.getLeadTime(), product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void should_decrement_available_when_in_stock() {
        // GIVEN
        Product product = new Product(null, 15, 3, "NORMAL", "RJ45 Cable", null, null, null);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        normalProcessor.process(product, LocalDate.now());

        // THEN
        assertEquals(2, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }
}