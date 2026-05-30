package com.example.ProductosJSS.repositories;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ProductosJSS.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCorreo(String correo);
    Optional<Usuario> findByCorreo(String correo);
}

