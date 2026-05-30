package com.example.ProductosJSS.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ProductosJSS.entities.Categoria;
import com.example.ProductosJSS.repositories.CategoriaRepository;

@Service
public class CategoriaServiceslmpl implements CategoriaServices{

    @Autowired
    private CategoriaRepository CategoriaRepositories;

    @Override
    public Categoria crear(Categoria unaCategoria){
        return CategoriaRepositories.save(unaCategoria);
    }

    @Override
    public Categoria obtenerId(Long id){
        return CategoriaRepositories.findById(id)
        .orElseThrow(()-> new RuntimeException("Categoria no encontrada"));
    }

    @Override
    public List<Categoria> ListarTodas() {
        return (List<Categoria>) CategoriaRepositories.findAll();
    }
    @Override
    public void eliminar(Long id){
        if (!CategoriaRepositories.existsById(id)){
            throw new RuntimeException("Categoria no encontrada");
        }
        CategoriaRepositories.deleteById(id);
    }

    @Override
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria existe =obtenerId(id);
        existe.setNombre(categoriaActualizada.getNombre());
        return existe;
    }



}
