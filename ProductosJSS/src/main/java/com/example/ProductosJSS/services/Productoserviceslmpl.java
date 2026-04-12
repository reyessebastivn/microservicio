package com.example.ProductosJSS.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ProductosJSS.entities.Producto;
import com.example.ProductosJSS.repositories.ProductoRepository;

@Service
public class Productoserviceslmpl implements ProductoServices {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto obtenerId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Override
    public List<Producto> listarTodas() {
        return productoRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: producto no encontrado con id " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    public Producto actualizar(Long id, Producto actualizado) {
        Producto existente = obtenerId(id);

        if (actualizado.getDescripcion() != null)
            existente.setDescripcion(actualizado.getDescripcion());
        if (actualizado.getNombre() != null)
            existente.setNombre(actualizado.getNombre());
        if (actualizado.getPrecio() != null)
            existente.setPrecio(actualizado.getPrecio());
        if (actualizado.getStock() != null)
            existente.setStock(actualizado.getStock());
        if (actualizado.getActivo() != null)
            existente.setActivo(actualizado.getActivo());

        return productoRepository.save(existente);
    }

    @Override
    public Producto desactivar(Long id) {
        Producto producto = obtenerId(id);

        // Si ya está desactivado, no hacemos nada
        if (Boolean.FALSE.equals(producto.getActivo())) {
            return producto;
        }

        producto.setActivo(false);
        return productoRepository.save(producto);
    }
}

