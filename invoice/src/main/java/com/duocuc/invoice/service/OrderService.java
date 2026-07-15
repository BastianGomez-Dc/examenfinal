package com.duocuc.invoice.service;

import com.duocuc.invoice.client.dto.RemoteOrder;

import java.util.Optional;

public interface OrderService {

    Optional<RemoteOrder> findOrder(Long orderId);
}
