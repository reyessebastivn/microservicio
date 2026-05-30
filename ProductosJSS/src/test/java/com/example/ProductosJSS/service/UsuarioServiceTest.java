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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.ProductosJSS.entities.Usuario;
import com.example.ProductosJSS.repositories.UsuarioRepository;
import com.example.ProductosJSS.services.UsuarioServicesImpl;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.obtenerId(99L);
        });

        assertEquals("Usuario no encontrado.", ex.getReason());
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void testCrear() {
        Usuario nuevo = new Usuario();
        nuevo.setNombre("Lucía");
        nuevo.setCorreo("lucia@correo.com");
        nuevo.setRun("12345678-9");
        nuevo.setRol("Cliente");
        nuevo.setPassword("Password123!");

        when(passwordEncoder.encode(any())).thenReturn("PasswordEncriptada123");
        when(repository.save(any(Usuario.class))).thenReturn(nuevo);

        Usuario resultado = service.crear(nuevo);

        assertNotNull(resultado);
        assertEquals("Lucía", resultado.getNombre());
        verify(passwordEncoder, times(1)).encode(any());
        verify(repository, times(1)).save(nuevo);
    }

    @Test
    void testCrearUsuarioConDatosValidos() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Pedro");
        usuario.setCorreo("pedro@correo.com");
        usuario.setRun("12345678-9");
        usuario.setRol("Cliente");
        usuario.setPassword("Password123!");

        when(passwordEncoder.encode(any())).thenReturn("PasswordEncriptada123");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = service.crear(usuario);

        assertNotNull(resultado);
        assertEquals("Pedro", resultado.getNombre());
        verify(passwordEncoder, times(1)).encode(any());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void testActualizar() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNombre("Antiguo");
        existente.setCorreo("a@a.com");
        existente.setRun("11111111-1");
        existente.setRol("Cliente");

        Usuario actualizacion = new Usuario();
        actualizacion.setNombre("Actualizado");
        actualizacion.setCorreo("nuevo@correo.com");
        actualizacion.setRun("22222222-2");
        actualizacion.setRol("Administrador");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = service.actualizar(1L, actualizacion);

        assertEquals("Actualizado", resultado.getNombre());
        assertEquals("nuevo@correo.com", resultado.getCorreo());
        assertEquals("Administrador", resultado.getRol());
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

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.eliminar(99L);
        });

        assertEquals("Usuario no encontrado.", ex.getReason());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void testDesactivar() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Paula");
        usuario.setCorreo("paula@correo.com");
        usuario.setRun("12345678-9");
        usuario.setRol("Cliente");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = service.desactivar(1L);

        assertNotNull(resultado);
        verify(repository, times(1)).save(any(Usuario.class));
    }
}