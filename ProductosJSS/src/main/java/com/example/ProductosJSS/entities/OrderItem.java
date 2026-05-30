package com.example.ProductosJSS.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dueña de la relación: Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    // Producto actual al momento de la compra (referencia)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Copias “snapshots” para auditoría
    @Column(length = 100)
    private String productoNombre;

    @Column(length = 30)
    private String sku;

    @NotNull
    @Min(0)
    private Long precioUnitario; // CLP sin decimales

    @NotNull
    @Min(1)
    private Integer cantidad;

    @NotNull
    @Min(0)
    private Long subtotal;

    @PrePersist
    public void prePersist() {
        if (subtotal == null) {
            subtotal = (precioUnitario != null ? precioUnitario : 0L) * (cantidad != null ? cantidad : 0);
        }
    }

    public void calcSubtotal() {
        this.subtotal = (precioUnitario != null ? precioUnitario : 0L) * (cantidad != null ? cantidad : 0);
    }
}
