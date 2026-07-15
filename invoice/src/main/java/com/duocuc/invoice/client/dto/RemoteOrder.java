package com.duocuc.invoice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

// Represents only the fields of the order service that the invoice service needs: status and total.
@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteOrder(Long id, String status, BigDecimal total) {}
