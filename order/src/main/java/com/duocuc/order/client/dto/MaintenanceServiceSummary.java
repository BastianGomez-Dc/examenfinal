package com.duocuc.order.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

// Represents only the fields of the service catalog that the order service needs
// to copy the price and name onto an order item.
@JsonIgnoreProperties(ignoreUnknown = true)
public record MaintenanceServiceSummary(Long id, String name, BigDecimal price) {}
