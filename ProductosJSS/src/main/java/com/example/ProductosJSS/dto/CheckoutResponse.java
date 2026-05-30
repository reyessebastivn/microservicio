package com.example.ProductosJSS.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponse {

    private Long orderId;
    private String status;
    private Long total;
    private LocalDateTime createdAt;
    private List<Line> lines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Line {
        private Long productId;
        private String sku;
        private String nombre;
        private Integer cantidad;
        private Long precioUnitario;
        private Long subtotal;
    }
}
