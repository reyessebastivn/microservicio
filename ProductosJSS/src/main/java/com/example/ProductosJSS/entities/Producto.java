package com.example.ProductosJSS.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "producto",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_producto_sku", columnNames = "sku")
    },
    indexes = {
        @Index(name = "idx_producto_nombre", columnList = "nombre"),
        @Index(name = "idx_producto_categoria", columnList = "categoria_id")
    }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Control de concurrencia optimista (opcional si usarás lock pesimista en el repositorio)
    @Version
    private Long version;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long precio;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stock;

    @JsonProperty(defaultValue = "true")
    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"productos"}) // evita recursión al serializar
    private Categoria categoria;

    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "El SKU solo puede contener letras, números, punto, guion y guion bajo")
    @Column(length = 30, nullable = false, unique = true)
    private String sku;

    // NUEVO: URL de imagen principal (si se usa almacenamiento externo) o ruta pública
    @Size(max = 500)
    @Column(length = 500)
    private String imagenUrl;

    /* ---------- Ciclo de vida ---------- */

    @PrePersist
    public void prePersist() {
        normalizarCampos();
        if (stock == null) stock = 0;
        if (activo == null) activo = true;
        // activo se sincroniza con stock
        this.activo = (this.stock != null && this.stock > 0);
    }

    @PreUpdate
    public void preUpdate() {
        normalizarCampos();
        // activo se sincroniza con stock
        this.activo = (this.stock != null && this.stock > 0);
    }

    private void normalizarCampos() {
        if (sku != null) sku = sku.trim();
        if (nombre != null) nombre = nombre.trim();
        if (descripcion != null) descripcion = descripcion.trim();
        if (imagenUrl != null) imagenUrl = imagenUrl.trim();
    }

    /* ---------- Helpers de dominio (opcionales pero útiles) ---------- */

    /** Disminuye stock asegurando que no quede negativo. Lanza IllegalArgumentException si no alcanza. */
    public void descontarStock(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad a descontar inválida");
        int current = this.stock != null ? this.stock : 0;
        if (cantidad > current) {
            throw new IllegalArgumentException("Stock insuficiente para el producto " + (sku != null ? sku : id));
        }
        this.stock = current - cantidad;
        this.activo = this.stock > 0;
    }

    /** Aumenta stock (para anulaciones/devoluciones). */
    public void aumentarStock(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad a aumentar inválida");
        int current = this.stock != null ? this.stock : 0;
        this.stock = current + cantidad;
        this.activo = this.stock > 0;
    }
}
