
package com.example.ProductosJSS.dto;

public record UsuarioDTO(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        String rol,
        Boolean activo
) {}
