package com.duocuc.order.service.impl;

import com.duocuc.order.client.dto.MaintenanceServiceSummary;
import com.duocuc.order.dto.OrderItemRequest;
import com.duocuc.order.dto.OrderItemResponse;
import com.duocuc.order.dto.OrderRequest;
import com.duocuc.order.dto.OrderResponse;
import com.duocuc.order.dto.StatusUpdateRequest;
import com.duocuc.order.entity.Order;
import com.duocuc.order.entity.OrderItem;
import com.duocuc.order.entity.OrderStatus;
import com.duocuc.order.exception.BusinessRuleException;
import com.duocuc.order.exception.ResourceNotFoundException;
import com.duocuc.order.repository.OrderRepository;
import com.duocuc.order.service.ClientService;
import com.duocuc.order.service.EquipmentService;
import com.duocuc.order.service.OrderService;
import com.duocuc.order.service.ServiceCatalogService;
import com.duocuc.order.service.TechnicianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final EquipmentService equipmentService;
    private final TechnicianService technicianService;
    private final ServiceCatalogService serviceCatalogService;

    public OrderServiceImpl(OrderRepository orderRepository, ClientService clientService,
                             EquipmentService equipmentService, TechnicianService technicianService,
                             ServiceCatalogService serviceCatalogService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.equipmentService = equipmentService;
        this.technicianService = technicianService;
        this.serviceCatalogService = serviceCatalogService;
    }

    @Override
    public OrderResponse save(OrderRequest request) {
        // Business rule: client, equipment and technician must exist in their respective microservices
        if (!clientService.existsClient(request.clientId())) {
            throw new BusinessRuleException("The specified client does not exist: " + request.clientId());
        }
        if (!equipmentService.existsEquipment(request.equipmentId())) {
            throw new BusinessRuleException("The specified equipment does not exist: " + request.equipmentId());
        }
        if (!technicianService.existsTechnician(request.technicianId())) {
            throw new BusinessRuleException("The specified technician does not exist: " + request.technicianId());
        }

        Order order = new Order();
        order.setClientId(request.clientId());
        order.setEquipmentId(request.equipmentId());
        order.setTechnicianId(request.technicianId());
        order.setStatus(OrderStatus.RECEIVED);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            // Business rule: the service must exist and its price is copied from the service catalog
            MaintenanceServiceSummary service = serviceCatalogService.findService(itemRequest.serviceId())
                    .orElseThrow(() -> new BusinessRuleException("The specified service does not exist: " + itemRequest.serviceId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setServiceId(service.id());
            item.setServiceName(service.name());
            item.setUnitPrice(service.price());
            item.setQuantity(itemRequest.quantity());
            items.add(item);

            total = total.add(service.price().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }
        order.setItems(items);
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        log.info("Order created with id={} total={}", saved.getId(), saved.getTotal());
        return toResponse(saved);
    }

    @Override
    public List<OrderResponse> findAll() {
        log.info("Listing all orders");
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    @Override
    public OrderResponse changeStatus(Long id, StatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Business rule: only sequential transitions are allowed RECEIVED -> IN_REPAIR -> READY -> DELIVERED
        if (!isValidTransition(order.getStatus(), request.newStatus())) {
            throw new BusinessRuleException(
                    "Invalid status transition: from " + order.getStatus() + " to " + request.newStatus());
        }
        order.setStatus(request.newStatus());
        Order updated = orderRepository.save(order);
        log.info("Order id={} changed status to {}", id, updated.getStatus());
        return toResponse(updated);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case RECEIVED -> next == OrderStatus.IN_REPAIR;
            case IN_REPAIR -> next == OrderStatus.READY;
            case READY -> next == OrderStatus.DELIVERED;
            case DELIVERED -> false;
        };
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getServiceId(), i.getServiceName(), i.getUnitPrice(), i.getQuantity(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();
        return new OrderResponse(
                o.getId(), o.getClientId(), o.getEquipmentId(), o.getTechnicianId(),
                o.getStatus(), o.getTotal(), o.getCreatedDate(), items
        );
    }
}
