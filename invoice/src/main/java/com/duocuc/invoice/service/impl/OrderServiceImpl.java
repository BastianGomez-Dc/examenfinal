package com.duocuc.invoice.service.impl;

import com.duocuc.invoice.client.OrderClient;
import com.duocuc.invoice.client.dto.RemoteOrder;
import com.duocuc.invoice.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Wraps the OrderClient: the rest of the service layer never depends on the HTTP client directly.
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderClient orderClient;

    public OrderServiceImpl(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public Optional<RemoteOrder> findOrder(Long orderId) {
        return orderClient.findOrder(orderId);
    }
}
