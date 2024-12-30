package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class ProductoServiceTest {

    @InjectMocks
    private ProductoService productoService;

    @Mock
    private ProductoRepository productoRepository;

    private ProductoModel productoModel;
    private ProductoDto productoDto;

    private final static Long productoId = 1L;
    private final static String nombre = "Test Producto";
    private final static String descripcion = "Test Descripcion";
    private final static Integer stock = 1;
    private final static double precio = 1.0;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        productoModel = ProductoModel.builder()
                .id(productoId)
                .nombre(nombre)
                .descripcion(descripcion)
                .stockDisponible(stock)
                .precio(precio)
                .build();

        productoDto = ProductoDto.builder()
                .id(productoId)
                .nombre(nombre)
                .descripcion(descripcion)
                .stockDisponible(stock)
                .precio(precio)
                .build();
    }

    @Test
    @DisplayName("Deberia crear un producto correctamente")
    void testCreateProductoSuccess() {
        // Preparacion
        when(productoRepository.save(any(ProductoModel.class)))
                .thenReturn(productoModel);

        // Ejecucion
        productoService.createProducto(productoDto);

        // Verificacion
        assertEquals(nombre, productoModel.getNombre());
        assertEquals(descripcion, productoModel.getDescripcion());
        assertEquals(stock, productoModel.getStockDisponible());
        assertEquals(precio, productoModel.getPrecio());
        verify(productoRepository, times(1)).save(any(ProductoModel.class));
    }

    @Test
    @DisplayName("Deberia lanzar una RuntimeException")
    void testCreateProductoFail() {
        // Preparacion
        when(productoRepository.save(any(ProductoModel.class)))
                .thenThrow(new RuntimeException("Error al guardar el producto"));

        // Ejecucion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoService.createProducto(productoDto);
        });

        // Verificacion
        assertEquals("Error al crear el producto.", exception.getMessage());
        verify(productoRepository, times(1)).save(any(ProductoModel.class));
    }

    @Test
    @DisplayName("Deberia retornar la informacion de un producto correctamente")
    void testGetProductoSuccess() {
        // Preparacion
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoModel));

        // Ejecucion
        ProductoDto result = productoService.getProductoById(productoId);

        // Verificacion
        assertNotNull(result);
        assertEquals(nombre, result.getNombre());
        assertEquals(descripcion, result.getDescripcion());
        assertEquals(stock, result.getStockDisponible());
        assertEquals(precio, result.getPrecio());
        verify(productoRepository, times(1)).findById(productoId);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFOundException")
    void testGetProductoNotFound() {
        // Preparacion
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.empty());

        // Ejecucion
        ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class, () -> {
            productoService.getProductoById(productoId);
        });

        // Verificacion
        assertEquals("Producto no encontrado con ID: " + productoId, exception.getMessage());
        verify(productoRepository, times(1)).findById(productoId);
    }

    @Test
    @DisplayName("Deberia actualizar los datos de un producto correctamente")
    void testUpdateProductoSuccess() {
        // Preparacion
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoModel));

        // Ejecucion
        productoService.updateProducto(productoId, productoDto);

        // Verificacion
        assertEquals(productoDto.getNombre(), productoModel.getNombre());
        assertEquals(productoDto.getDescripcion(), productoModel.getDescripcion());
        assertEquals(productoDto.getStockDisponible(), productoModel.getStockDisponible());
        assertEquals(productoDto.getPrecio(), productoModel.getPrecio());
        verify(productoRepository, times(1)).save(productoModel);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFoundException")
    void testProductoNotFound() {
        // Preparacion
        when(productoRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Ejecucion
        ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class, () -> {
            productoService.updateProducto(1L, productoDto);
        });

        // Verificacion
        assertEquals("Producto no encontrado con ID: " + productoId, exception.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deberia eliminar un producto correctamente")
    void testDeleteProductoSUccess() {
        // Preparacion
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoModel));

        // Ejecucion
        productoService.deleteProducto(productoId);

        // Verificacion
        verify(productoRepository, times(1)).delete(productoModel);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFoundException")
    void testDeeleteProductoNotFound() {
        // Preparacion
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.empty());

        // Ejecucion
        ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class, () -> {
            productoService.deleteProducto(productoId);
        });

        // Verificacion
        assertEquals("Producto no encontrado con ID: " + productoId, exception.getMessage());
        verify(productoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deberia retornar todos los productos de la base de datos correctamente")
    void testGetAllProductos() {
        // Preparacion
        ProductoModel productoModel2 = ProductoModel.builder()
                .id(2L)
                .nombre("Test Producto 2")
                .descripcion("Test Descripcion 2")
                .stockDisponible(9)
                .precio(20.0)
                .build();

        when(productoRepository.findAll())
                .thenReturn(List.of(productoModel, productoModel2));

        // Ejecucion
        List<ProductoDto> resultado = productoService.getAllProductos(0, 5);

        // Verificacion
        assertNotNull(resultado);

        assertEquals(2, resultado.size());
        assertEquals(nombre, resultado.get(0).getNombre());

        assertEquals(descripcion, resultado.get(0).getDescripcion());
        assertEquals(stock, resultado.get(0).getStockDisponible());
        assertEquals(precio, resultado.get(0).getPrecio());

        assertEquals(productoModel2.getNombre(), resultado.get(1).getNombre());
        assertEquals(productoModel2.getDescripcion(), resultado.get(1).getDescripcion());
        assertEquals(productoModel2.getStockDisponible(), resultado.get(1).getStockDisponible());
        assertEquals(productoModel2.getPrecio(), resultado.get(1).getPrecio());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deberia retornar una lista vacia")
    void testGetAllProductosEmpty() {
        // Preparacion
        when(productoRepository.findAll())
                .thenReturn(List.of());

        // Ejecucion
        List<ProductoDto> resultado = productoService.getAllProductos(0, 5);

        // Verificacion
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoRepository, times(1)).findAll();
    }
}
