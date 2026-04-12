package com.example.ProductosJSS.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;   // ← RequestBody de SPRING
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ProductosJSS.entities.Producto;
import com.example.ProductosJSS.services.Productoserviceslmpl;
import com.example.ProductosJSS.repositories.ProductoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;                           // ← Valid de Jakarta
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    private final Productoserviceslmpl productoService;
    // 🔹 Agregado: repositorio para usar los métodos de filtrado sin tocar tu service actual
    private final ProductoRepository productoRepository;

    @Operation(summary = "Listar todos los productos", description = "Devuelve una lista con todos los productos disponibles.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodas());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto específico mediante su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        Producto p = productoService.obtenerId(id);
        return ResponseEntity.ok(p);
    }

    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en la base de datos.")
    @ApiResponse(responseCode = "201", description = "Producto creado correctamente")
    @PostMapping
    public ResponseEntity<Producto> crear(
        // ← Descripción para Swagger (opcional, no afecta ejecución)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON del producto a crear"
        )
        @Valid @RequestBody Producto p // ← RequestBody de Spring + validación
    ) {
        Producto nuevo = productoService.crear(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Editar producto existente", description = "Actualiza los datos de un producto por su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> editar(
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON con los datos a actualizar"
        )
        @Valid @RequestBody Producto p
    ) {
        Producto actualizado = productoService.actualizar(id, p);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------------
    // 🔹 NUEVO: Endpoint de filtrado (categoría + activo)
    // ---------------------------------------------
    @Operation(
        summary = "Filtrar productos",
        description = "Filtra por categoría (id o nombre) y por estado activo. Si no envías parámetros, devuelve solo activos."
    )
    @GetMapping("/filtrar")
    public ResponseEntity<List<Producto>> filtrar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String categoriaNombre,
            @RequestParam(required = false, defaultValue = "true") Boolean activo
    ) {
        List<Producto> out;

        if (Boolean.TRUE.equals(activo)) {
            if (categoriaId != null) {
                out = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
            } else if (categoriaNombre != null && !categoriaNombre.isBlank()) {
                out = productoRepository.findByCategoriaNombreIgnoreCaseAndActivoTrue(categoriaNombre.trim());
            } else {
                out = productoRepository.findByActivoTrue();
            }
        } else {
            // activo=false → traer todos; si llega categoría, filtrar en memoria
            if (categoriaId != null || (categoriaNombre != null && !categoriaNombre.isBlank())) {
                String nom = categoriaNombre == null ? null : categoriaNombre.trim();
                out = productoRepository.findAll().stream()
                        .filter(p -> {
                            if (categoriaId != null) {
                                return p.getCategoria() != null && categoriaId.equals(p.getCategoria().getId());
                            } else if (nom != null) {
                                return p.getCategoria() != null
                                        && p.getCategoria().getNombre() != null
                                        && p.getCategoria().getNombre().equalsIgnoreCase(nom);
                            }
                            return true;
                        })
                        .toList();
            } else {
                out = productoRepository.findAll();
            }
        }

        return ResponseEntity.ok(out);
    }
}
