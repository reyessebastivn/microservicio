package com.example.ProductosJSS.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ProductosJSS.dto.CheckoutItemDTO;
import com.example.ProductosJSS.dto.CheckoutRequest;
import com.example.ProductosJSS.dto.CheckoutResponse;
import com.example.ProductosJSS.entities.*;
import com.example.ProductosJSS.repositories.OrderRepository;
import com.example.ProductosJSS.repositories.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductoRepository productoRepository;
    private final OrderRepository orderRepository;

    /**
     * Realiza el checkout:
     * - Bloquea los productos involucrados (PESSIMISTIC_WRITE)
     * - Valida stock
     * - Descuenta stock
     * - Crea Order + OrderItems
     */
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Carrito vacío");
        }

        // Agrupar por productoId para consolidar cantidades repetidas
        Map<Long, Integer> quantities = new HashMap<>();
        for (CheckoutItemDTO it : request.getItems()) {
            if (it.getProductId() == null || it.getQuantity() == null || it.getQuantity() < 1) {
                throw new IllegalArgumentException("Ítem inválido en el carrito");
            }
            quantities.merge(it.getProductId(), it.getQuantity(), Integer::sum);
        }

        // Bloqueo pesimista: lee todos los productos y los bloquea para esta tx
        List<Long> ids = new ArrayList<>(quantities.keySet());
        List<Producto> productos = productoRepository.findAllForUpdateByIdIn(ids);

        // Validar existencia
        if (productos.size() != ids.size()) {
            Set<Long> found = productos.stream().map(Producto::getId).collect(Collectors.toSet());
            List<Long> missing = ids.stream().filter(id -> !found.contains(id)).toList();
            throw new IllegalArgumentException("Productos inexistentes: " + missing);
        }

        // Validar stock
        for (Producto p : productos) {
            int requested = quantities.getOrDefault(p.getId(), 0);
            int stock = Optional.ofNullable(p.getStock()).orElse(0);
            if (requested > stock) {
                throw new IllegalStateException("Stock insuficiente para SKU " + p.getSku() +
                        " (solicitado " + requested + ", disponible " + stock + ")");
            }
            if (Boolean.FALSE.equals(p.getActivo())) {
                throw new IllegalStateException("Producto inactivo: " + (p.getSku() != null ? p.getSku() : p.getId()));
            }
        }

        // Descontar stock y calcular totales
        long total = 0L;
        Order order = Order.builder()
                .clienteEmail(request.getEmail())
                .status(OrderStatus.CREADA)
                .total(0L)
                .build();

        for (Producto p : productos) {
            int qty = quantities.get(p.getId());
            int nuevoStock = p.getStock() - qty;
            p.setStock(nuevoStock);
            if (nuevoStock <= 0) {
                p.setActivo(false); // opcional: desactivar cuando queda en 0
            }

            long precioUnit = Optional.ofNullable(p.getPrecio()).orElse(0L);
            long subtotal = precioUnit * qty;
            total += subtotal;

            OrderItem item = OrderItem.builder()
                    .producto(p)
                    .productoNombre(p.getNombre())
                    .sku(p.getSku())
                    .precioUnitario(precioUnit)
                    .cantidad(qty)
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);
        }

        order.setTotal(total);

        // Persistir: guarda Order + Items, y sincroniza Productos con nuevo stock
        Order saved = orderRepository.save(order);
        productoRepository.saveAll(productos);

        // Armar respuesta
        List<CheckoutResponse.Line> lines = saved.getItems().stream()
            .map(i -> CheckoutResponse.Line.builder()
                .productId(i.getProducto() != null ? i.getProducto().getId() : null)
                .sku(i.getSku())
                .nombre(i.getProductoNombre())
                .cantidad(i.getCantidad())
                .precioUnitario(i.getPrecioUnitario())
                .subtotal(i.getSubtotal())
                .build())
            .toList();

        return CheckoutResponse.builder()
                .orderId(saved.getId())
                .status(saved.getStatus().name())
                .total(saved.getTotal())
                .createdAt(saved.getCreatedAt())
                .lines(lines)
                .build();
    }
}
