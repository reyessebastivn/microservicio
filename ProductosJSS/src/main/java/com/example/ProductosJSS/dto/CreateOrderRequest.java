package com.example.ProductosJSS.dto;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    String clienteEmail,
    @NotNull List<Item> items
) {
    public static record Item(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad
    ){}
}
