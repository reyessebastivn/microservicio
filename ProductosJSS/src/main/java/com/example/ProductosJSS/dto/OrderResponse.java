package com.example.ProductosJSS.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String status,
    Long total,
    String clienteEmail,
    LocalDateTime createdAt,
    List<OrderItemDTO> items
) {
    public record OrderItemDTO(
        Long productoId,
        String nombreProducto,
        Long precioUnitario,
        Integer cantidad,
        Long subtotal
    ) {}
}
