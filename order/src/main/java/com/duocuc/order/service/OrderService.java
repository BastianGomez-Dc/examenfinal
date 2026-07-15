package com.duocuc.order.service;

import com.duocuc.order.dto.OrderRequest;
import com.duocuc.order.dto.OrderResponse;
import com.duocuc.order.dto.StatusUpdateRequest;

import java.util.List;

public interface OrderService {

    OrderResponse save(OrderRequest request);

    List<OrderResponse> findAll();

    OrderResponse findById(Long id);

    OrderResponse changeStatus(Long id, StatusUpdateRequest request);
}
