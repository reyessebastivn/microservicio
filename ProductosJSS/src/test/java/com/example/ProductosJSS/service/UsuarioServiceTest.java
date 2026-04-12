package com.example.ProductosJSS.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.ProductosJSS.entities.Usuario;
import com.example.ProductosJSS.repositories.UsuarioRepository;
import com.example.ProductosJSS.services.UsuarioServicesImpl;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioServicesImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testListarTodos() {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNombre("Pablo");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNombre("Ana");

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<Usuario> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Pablo", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }


    @Test
    void testObtenerPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = service.obtenerId(1L);

        assertEquals("Carlos", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorIdNoExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.obtenerId(99L);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(repository, times(1)).findById(99L);
    }


    @Test
    void testCrear() {
        Usuario nuevo = new Usuario();
        nuevo.setId(1L);
        nuevo.setNombre("Lucía");

        when(repository.existsById(null)).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenReturn(nuevo);

        Usuario resultado = service.crear(nuevo);

        assertNotNull(resultado);
        assertEquals("Lucía", resultado.getNombre());
        verify(repository, times(1)).save(nuevo);
    }

    @Test
    void testCrearUsuarioYaExistente() {
        Usuario duplicado = new Usuario();
        duplicado.setId(1L);
        duplicado.setNombre("Pedro");

        when(repository.existsById(null)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.crear(duplicado);
        });

        assertEquals("El correo ya está registrado", ex.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }


    @Test
    void testActualizar() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNombre("Antiguo");
        existente.setCorreo("a@a.com");
        existente.setRol("Cliente");

        Usuario actualizacion = new Usuario();
        actualizacion.setNombre("Actualizado");
        actualizacion.setCorreo("nuevo@correo.com");
        actualizacion.setRol("Admin");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = service.actualizar(1L, actualizacion);

        assertEquals("Actualizado", resultado.getNombre());
        assertEquals("nuevo@correo.com", resultado.getCorreo());
        assertEquals("Admin", resultado.getRol());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testEliminar() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarNoExistente() {
        when(repository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.eliminar(99L);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }


    @Test
    void testDesactivar() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Paula");
        usuario.setRol("Admin");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = service.desactivar(1L);

        assertEquals("Inactivo", resultado.getRol());
        verify(repository, times(1)).save(any(Usuario.class));
    }
}
