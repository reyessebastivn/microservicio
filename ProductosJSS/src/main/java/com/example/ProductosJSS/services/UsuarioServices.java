package com.example.ProductosJSS.services;

import java.util.List;

import com.example.ProductosJSS.entities.Usuario;

public interface UsuarioServices {

    Usuario crear(Usuario usuario);
    Usuario obtenerId(Long id);
    List<Usuario> listarTodos();
    void eliminar(Long id);
    Usuario actualizar(Long id, Usuario usuarioActualizado);
    Usuario desactivar(Long id); // cambia el estado del usuario a inactivo (por ejemplo)
}

