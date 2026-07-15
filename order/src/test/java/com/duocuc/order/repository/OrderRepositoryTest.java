package com.duocuc.order.repository;

import com.duocuc.order.entity.Order;
import com.duocuc.order.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("findAll: returns the 5 orders loaded by the seed migration")
    void findAll_shouldReturnSeedOrders() {
        assertEquals(5, orderRepository.findAll().size());
    }

    @Test
    @DisplayName("findById: returns the order with its items loaded (OneToMany relationship)")
    void findById_shouldReturnOrderWithItems() {
        Optional<Order> order = orderRepository.findById(1L);

        assertTrue(order.isPresent());
        assertEquals(OrderStatus.RECEIVED, order.get().getStatus());
        assertEquals(1, order.get().getItems().size());
    }
}
