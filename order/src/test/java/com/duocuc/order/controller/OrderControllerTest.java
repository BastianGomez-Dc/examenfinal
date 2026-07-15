package com.duocuc.order.controller;

import com.duocuc.order.dto.OrderItemRequest;
import com.duocuc.order.dto.OrderRequest;
import com.duocuc.order.dto.OrderResponse;
import com.duocuc.order.dto.StatusUpdateRequest;
import com.duocuc.order.entity.OrderStatus;
import com.duocuc.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderResponse = new OrderResponse(
                1L, 1L, 1L, 1L, OrderStatus.RECEIVED, new BigDecimal("15000.00"), null, List.of());
    }

    @Test
    @DisplayName("create: returns 201 with the created order")
    void create_shouldReturn201() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1L, 1L, List.of(new OrderItemRequest(1L, 1)));
        when(orderService.save(any(OrderRequest.class))).thenReturn(orderResponse);

        // When
        ResponseEntity<OrderResponse> result = orderController.create(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(OrderStatus.RECEIVED, result.getBody().status());
    }

    @Test
    @DisplayName("findAll: returns 200 with the list of orders")
    void findAll_shouldReturn200() {
        // Given
        when(orderService.findAll()).thenReturn(List.of(orderResponse));

        // When
        ResponseEntity<List<OrderResponse>> result = orderController.findAll();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    @DisplayName("findById: returns 200 with the order found")
    void findById_shouldReturn200() {
        // Given
        when(orderService.findById(1L)).thenReturn(orderResponse);

        // When
        ResponseEntity<OrderResponse> result = orderController.findById(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
    }

    @Test
    @DisplayName("changeStatus: returns 200 with the new status")
    void changeStatus_shouldReturn200() {
        // Given
        StatusUpdateRequest request = new StatusUpdateRequest(OrderStatus.IN_REPAIR);
        OrderResponse updated = new OrderResponse(1L, 1L, 1L, 1L, OrderStatus.IN_REPAIR, new BigDecimal("15000.00"), null, List.of());
        when(orderService.changeStatus(1L, request)).thenReturn(updated);

        // When
        ResponseEntity<OrderResponse> result = orderController.changeStatus(1L, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(OrderStatus.IN_REPAIR, result.getBody().status());
    }
}
