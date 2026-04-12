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
import org.springframework.web.bind.annotation.RequestBody; // ✅ SPRING, no Swagger
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ProductosJSS.dto.UsuarioDTO;
import com.example.ProductosJSS.entities.Usuario;
import com.example.ProductosJSS.services.UsuarioServicesImpl;

import io.swagger.v3.oas.annotations.Operation;
// ⛔️ NO importes io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// Si prefieres @Valid, importa:
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioServicesImpl usuarioService;

    private UsuarioDTO toDto(Usuario u) {
        return new UsuarioDTO(
                u.getId(),
                u.getRun(),
                u.getNombre(),
                u.getApellidos(),
                u.getCorreo(),
                u.getRol(),
                u.getActivo()
        );
    }

    private List<UsuarioDTO> toDtoList(List<Usuario> list) {
        return list.stream().map(this::toDto).toList();
    }
    // ---------------------------------------------------------

    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(toDtoList(usuarioService.listarTodos()));
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario mediante su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        Usuario u = usuarioService.obtenerId(id);
        return ResponseEntity.ok(toDto(u));
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario en la base de datos.")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody @Valid Usuario u) { // ✅ sigue siendo JSON; dispara validaciones
        Usuario nuevo = usuarioService.crear(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(nuevo));
    }

    @Operation(summary = "Editar usuario existente", description = "Actualiza los datos de un usuario mediante su ID.")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> editar(@PathVariable Long id, @RequestBody @Valid Usuario u) {
        Usuario actualizado = usuarioService.actualizar(id, u);
        return ResponseEntity.ok(toDto(actualizado));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar usuario", description = "Marca al usuario como inactivo sin cambiar su rol.")
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioDTO> desactivar(@PathVariable Long id) {
        Usuario u = usuarioService.desactivar(id);
        return ResponseEntity.ok(toDto(u));
    }
}
