package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void processOrderShouldReturnOk_and_not_regress() throws Exception {
        // GIVEN
        List<Product> products = createProducts();
        Set<Product> orderItems = new HashSet<>(products);

        productRepository.saveAll(products);

        Order order = createOrder(orderItems);
        order = orderRepository.save(order);

        // WHEN
        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // THEN (au minimum: la commande existe toujours)
        Order resultOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(order.getId(), resultOrder.getId());
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        LocalDate today = LocalDate.now();

        List<Product> products = new ArrayList<>();
        products.add(new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null));
        products.add(new Product(null, 10, 0, "NORMAL", "USB Dongle", null, null, null));

        products.add(new Product(null, 15, 30, "EXPIRABLE", "Butter",
                today.plusDays(26), null, null));
        products.add(new Product(null, 90, 6, "EXPIRABLE", "Milk",
                today.minusDays(2), null, null));

        products.add(new Product(null, 15, 30, "SEASONAL", "Watermelon",
                null, today.minusDays(2), today.plusDays(58)));
        products.add(new Product(null, 15, 30, "SEASONAL", "Grapes",
                null, today.plusDays(180), today.plusDays(240)));

        return products;
    }
}