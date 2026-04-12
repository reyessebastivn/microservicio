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

import com.example.ProductosJSS.entities.Categoria;
import com.example.ProductosJSS.repositories.CategoriaRepository;
import com.example.ProductosJSS.services.CategoriaServiceslmpl;


public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceslmpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testListarTodas() {
        // Datos simulados
        Categoria c1 = new Categoria();
        c1.setId(1L);
        c1.setNombre("Accesorios");

        Categoria c2 = new Categoria();
        c2.setId(2L);
        c2.setNombre("Joyas");

        when(repository.findAll()).thenReturn(List.of(c1, c2));

        // Ejecución
        List<Categoria> resultado = service.ListarTodas();

        // Verificaciones
        assertEquals(2, resultado.size());
        assertEquals("Accesorios", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }


    @Test
    void testObtenerPorId() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Anillos");

        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = service.obtenerId(1L);

        assertEquals("Anillos", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
    }


    @Test
    void testCrear() {
        Categoria nueva = new Categoria();
        nueva.setNombre("Pulseras");

        when(repository.save(any(Categoria.class))).thenReturn(nueva);

        Categoria resultado = service.crear(nueva);

        assertEquals("Pulseras", resultado.getNombre());
        verify(repository, times(1)).save(nueva);
    }

    
    @Test
    void testActualizar() {
        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setNombre("Vieja");

        Categoria actualizada = new Categoria();
        actualizada.setNombre("Nueva");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        Categoria resultado = service.actualizar(1L, actualizada);

        assertEquals("Nueva", resultado.getNombre());
        verify(repository, times(1)).save(any(Categoria.class));
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.eliminar(99L);
        });

        assertEquals("Categoría no encontrada", exception.getMessage());
        verify(repository, never()).deleteById(any());
    }

    // 
    @Test
    void testObtenerPorIdNoExistente() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.obtenerId(5L);
        });

        assertEquals("Categoría no encontrada", exception.getMessage());
        verify(repository, times(1)).findById(5L);
    }
}
