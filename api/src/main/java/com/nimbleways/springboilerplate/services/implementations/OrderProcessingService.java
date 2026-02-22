package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final List<ProductProcessor> processors;

    @Transactional
    public ProcessOrderResponse process(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        Map<ProductType, ProductProcessor> byType = processors.stream()
                .collect(Collectors.toMap(ProductProcessor::supports, Function.identity()));

        LocalDate today = LocalDate.now();

        for (Product product : order.getItems()) {
            ProductType type = ProductType.from(product.getType());
            ProductProcessor processor = byType.get(type);
            if (processor == null) {
                throw new IllegalStateException("No processor found for type: " + type);
            }
            processor.process(product, today);
        }

        return new ProcessOrderResponse(order.getId());
    }
}