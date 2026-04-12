package com.example.ProductosJSS.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.example.ProductosJSS.entities.Producto;

import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findTop4ByActivoTrueOrderByIdDesc();

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    List<Producto> findByCategoriaNombreIgnoreCaseAndActivoTrue(String nombre);

    // 👇 MÉTODO QUE USA TU OrderService.checkout()
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id IN :ids")
    List<Producto> findAllForUpdateByIdIn(@Param("ids") List<Long> ids);
}
