package com.duocuc.invoice.client;

import com.duocuc.invoice.client.dto.RemoteOrder;

import java.util.Optional;

public interface OrderClient {

    // Returns the order (with its current status and total) or empty if it does not exist in the order service.
    Optional<RemoteOrder> findOrder(Long orderId);
}
