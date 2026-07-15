package com.duocuc.order.service;

import com.duocuc.order.client.dto.MaintenanceServiceSummary;
import com.duocuc.order.dto.OrderItemRequest;
import com.duocuc.order.dto.OrderRequest;
import com.duocuc.order.dto.OrderResponse;
import com.duocuc.order.dto.StatusUpdateRequest;
import com.duocuc.order.entity.Order;
import com.duocuc.order.entity.OrderItem;
import com.duocuc.order.entity.OrderStatus;
import com.duocuc.order.exception.BusinessRuleException;
import com.duocuc.order.exception.ResourceNotFoundException;
import com.duocuc.order.repository.OrderRepository;
import com.duocuc.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ClientService, EquipmentService, TechnicianService and ServiceCatalogService (local wrappers
 * around their respective clients) are mocked because they represent remote communication with
 * the other 4 microservices the order service consumes.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private EquipmentService equipmentService;

    @Mock
    private TechnicianService technicianService;

    @Mock
    private ServiceCatalogService serviceCatalogService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setClientId(1L);
        order.setEquipmentId(1L);
        order.setTechnicianId(1L);
        order.setStatus(OrderStatus.RECEIVED);
        order.setTotal(new BigDecimal("15000.00"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setServiceId(1L);
        item.setServiceName("General diagnostics");
        item.setUnitPrice(new BigDecimal("15000.00"));
        item.setQuantity(1);
        order.setItems(List.of(item));
    }

    @Test
    @DisplayName("save: creates an order when client, equipment, technician and service exist")
    void save_shouldCreateOrder_whenDataIsValid() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1L, 1L, List.of(new OrderItemRequest(1L, 1)));
        when(clientService.existsClient(1L)).thenReturn(true);
        when(equipmentService.existsEquipment(1L)).thenReturn(true);
        when(technicianService.existsTechnician(1L)).thenReturn(true);
        when(serviceCatalogService.findService(1L)).thenReturn(Optional.of(new MaintenanceServiceSummary(1L, "General diagnostics", new BigDecimal("15000.00"))));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponse response = orderService.save(request);

        // Then
        assertEquals(OrderStatus.RECEIVED, response.status());
        assertEquals(new BigDecimal("15000.00"), response.total());
        assertEquals(1, response.items().size());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the client does not exist")
    void save_shouldThrowException_whenClientDoesNotExist() {
        // Given
        OrderRequest request = new OrderRequest(999L, 1L, 1L, List.of(new OrderItemRequest(1L, 1)));
        when(clientService.existsClient(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the equipment does not exist")
    void save_shouldThrowException_whenEquipmentDoesNotExist() {
        // Given
        OrderRequest request = new OrderRequest(1L, 999L, 1L, List.of(new OrderItemRequest(1L, 1)));
        when(clientService.existsClient(1L)).thenReturn(true);
        when(equipmentService.existsEquipment(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the technician does not exist")
    void save_shouldThrowException_whenTechnicianDoesNotExist() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1L, 999L, List.of(new OrderItemRequest(1L, 1)));
        when(clientService.existsClient(1L)).thenReturn(true);
        when(equipmentService.existsEquipment(1L)).thenReturn(true);
        when(technicianService.existsTechnician(999L)).thenReturn(false);

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("save: throws BusinessRuleException when the service does not exist")
    void save_shouldThrowException_whenServiceDoesNotExist() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1L, 1L, List.of(new OrderItemRequest(999L, 1)));
        when(clientService.existsClient(1L)).thenReturn(true);
        when(equipmentService.existsEquipment(1L)).thenReturn(true);
        when(technicianService.existsTechnician(1L)).thenReturn(true);
        when(serviceCatalogService.findService(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException when it does not exist")
    void findById_shouldThrowException_whenNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.findById(99L));
    }

    @Test
    @DisplayName("changeStatus: transitions from RECEIVED to IN_REPAIR")
    void changeStatus_shouldTransition_fromReceivedToInRepair() {
        // Given
        StatusUpdateRequest request = new StatusUpdateRequest(OrderStatus.IN_REPAIR);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        OrderResponse response = orderService.changeStatus(1L, request);

        // Then
        assertEquals(OrderStatus.IN_REPAIR, response.status());
    }

    @Test
    @DisplayName("changeStatus: throws BusinessRuleException when the transition skips statuses")
    void changeStatus_shouldThrowException_whenTransitionSkipsStatuses() {
        // Given
        StatusUpdateRequest request = new StatusUpdateRequest(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.changeStatus(1L, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("changeStatus: throws BusinessRuleException when the order is already delivered")
    void changeStatus_shouldThrowException_whenOrderAlreadyDelivered() {
        // Given
        order.setStatus(OrderStatus.DELIVERED);
        StatusUpdateRequest request = new StatusUpdateRequest(OrderStatus.RECEIVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When / Then
        assertThrows(BusinessRuleException.class, () -> orderService.changeStatus(1L, request));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
