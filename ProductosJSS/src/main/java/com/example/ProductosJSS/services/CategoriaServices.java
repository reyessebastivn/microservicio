package com.example.ProductosJSS.services;

import java.util.List;

import com.example.ProductosJSS.entities.Categoria;

public interface CategoriaServices {

    Categoria crear(Categoria unaCategoria);
    Categoria obtenerId(Long id);
    List<Categoria> ListarTodas();
    void eliminar(Long id);
    Categoria actualizar(Long id, Categoria categoriaActualizada);

}