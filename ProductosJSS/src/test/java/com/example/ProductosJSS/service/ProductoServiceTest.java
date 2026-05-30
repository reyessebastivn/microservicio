package com.example.ProductosJSS.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.ProductosJSS.entities.Producto;
import com.example.ProductosJSS.repositories.ProductoRepository;
import com.example.ProductosJSS.services.Productoserviceslmpl;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private Productoserviceslmpl service; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   
    @Test
    void testListarTodas() {
        // Datos simulados
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Collar");
        p1.setDescripcion("Collar de plata");
        p1.setPrecio(10000L);
        p1.setStock(20);
        p1.setActivo(true);

        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Anillo");
        p2.setDescripcion("Anillo de oro");
        p2.setPrecio(15000L);
        p2.setStock(15);
        p2.setActivo(true);

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Producto> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Collar", resultado.get(0).getNombre());
        verify(repository, times(1)).findAll();
    }


    @Test
    void testObtenerPorId() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Pulsera");
        producto.setDescripcion("Pulsera de cuero");
        producto.setPrecio(5000L);
        producto.setStock(10);
        producto.setActivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = service.obtenerId(1L);

        assertEquals("Pulsera", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
    }

  
    @Test
    void testActualizar() {
        Producto original = new Producto();
        original.setId(1L);
        original.setNombre("Anillo viejo");
        original.setDescripcion("Anillo antiguo");
        original.setPrecio(12000L);
        original.setStock(5);
        original.setActivo(true);

        Producto actualizado = new Producto();
        actualizado.setNombre("Anillo nuevo");
        actualizado.setDescripcion("Anillo moderno");
        actualizado.setPrecio(18000L);
        actualizado.setStock(8);
        actualizado.setActivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(original));
        when(repository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto resultado = service.actualizar(1L, actualizado);

        assertEquals("Anillo nuevo", resultado.getNombre());
        assertEquals(18000, resultado.getPrecio());
        verify(repository, times(1)).save(any(Producto.class));
    }


    @Test
    void testEliminar() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

  
    @Test
    void testDesactivar() {
        Producto activo = new Producto();
        activo.setId(1L);
        activo.setNombre("Collar");
        activo.setActivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(activo));
        when(repository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto resultado = service.desactivar(1L);

        assertFalse(resultado.getActivo());
        verify(repository, times(1)).save(any(Producto.class));
    }
}
