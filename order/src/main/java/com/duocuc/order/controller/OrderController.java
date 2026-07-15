package com.duocuc.order.controller;

import com.duocuc.order.dto.OrderRequest;
import com.duocuc.order.dto.OrderResponse;
import com.duocuc.order.dto.StatusUpdateRequest;
import com.duocuc.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "TecnoFix maintenance work orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create a work order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "422", description = "Client, equipment, technician or service does not exist"),
            @ApiResponse(responseCode = "502", description = "A remote microservice is unavailable")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all orders")
    @ApiResponse(responseCode = "200", description = "List of orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @Operation(summary = "Get an order by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order does not exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Operation(summary = "Change the status of an order (RECEIVED → IN_REPAIR → READY → DELIVERED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Order does not exist"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> changeStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.changeStatus(id, request));
    }
}
