package com.example.ProductosJSS.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ProductosJSS.entities.Usuario;
import com.example.ProductosJSS.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServicesImpl implements UsuarioServices {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario crear(Usuario usuario) {
        // RUN obligatorio (sin validar formato real)
        if (isBlank(usuario.getRun())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUN es obligatorio.");
        }

        // Correo obligatorio + normalizado + único
        if (isBlank(usuario.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio.");
        }
        String correo = usuario.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo.");
        }
        usuario.setCorreo(correo);

        // Password obligatoria (se cifra)
        if (isBlank(usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria.");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Rol (default Cliente si no viene)
        if (isBlank(usuario.getRol())) {
            usuario.setRol("Cliente");
        } else {
            validarRol(usuario.getRol());
        }

        // Activo por defecto
        usuario.setActivo(true);

        // No permitir crear con id de otro existente
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe.");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Usuario obtenerId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario existente = obtenerId(id);

        // RUN (opcional en actualización; si viene, que no sea vacío)
        if (notBlank(usuarioActualizado.getRun())) {
            existente.setRun(usuarioActualizado.getRun());
        }

        if (notBlank(usuarioActualizado.getNombre())) {
            existente.setNombre(usuarioActualizado.getNombre());
        }
        if (notBlank(usuarioActualizado.getApellidos())) {
            existente.setApellidos(usuarioActualizado.getApellidos());
        }

        // Correo (normalizado + unicidad)
        if (notBlank(usuarioActualizado.getCorreo())) {
            String nuevoCorreo = usuarioActualizado.getCorreo().trim().toLowerCase();
            if (!nuevoCorreo.equals(existente.getCorreo()) && usuarioRepository.existsByCorreo(nuevoCorreo)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo.");
            }
            existente.setCorreo(nuevoCorreo);
        }

        // Password (si viene, se cifra)
        if (notBlank(usuarioActualizado.getPassword())) {
            existente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        // Rol (si viene, validar catálogo)
        if (notBlank(usuarioActualizado.getRol())) {
            validarRol(usuarioActualizado.getRol());
            existente.setRol(usuarioActualizado.getRol().trim());
        }

        // Activo (si viene, aplicar)
        if (usuarioActualizado.getActivo() != null) {
            existente.setActivo(usuarioActualizado.getActivo());
        }

        return usuarioRepository.save(existente);
    }

    @Override
    public Usuario desactivar(Long id) {
        Usuario existente = obtenerId(id);
        existente.setActivo(false);
        return usuarioRepository.save(existente);
    }

    // --------- helpers ----------
    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
    private static void validarRol(String rol) {
        String r = rol.trim();
        if (!r.equalsIgnoreCase("Administrador") && !r.equalsIgnoreCase("Cliente")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido. Use 'Administrador' o 'Cliente'.");
        }
    }
}
