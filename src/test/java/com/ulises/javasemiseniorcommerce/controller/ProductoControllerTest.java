package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ApiResponse;
import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.service.ProductoService;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class ProductoControllerTest {

    @InjectMocks
    private ProductoController productoController;

    @Mock
    private ProductoService productoService;

    private ProductoDto productoDto;

    private final static Long productoId = 1L;
    private final static String productoNombre = "Producto Test";
    private final static String productoDescripcion = "Descripcion";
    private final static Double precioProducto = 100.0;
    private final static Integer stockProducto = 10;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        productoDto = ProductoDto.builder()
                .id(productoId)
                .nombre(productoNombre)
                .descripcion(productoDescripcion)
                .stockDisponible(stockProducto)
                .precio(precioProducto)
                .build();
    }

    @Test
    @DisplayName("Deberia Crear un producto correctamente")
    void testCreateProductoSuccess() {
        // Preparacion
        doNothing().when(productoService).createProducto(productoDto);

        // Ejecucion
        ResponseEntity<?> response = productoController.createProducto(productoDto);

        // Verificacion
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assert apiResponse != null;
        assertTrue(apiResponse.isSuccess());
        assertEquals("Producto creado exitosamente", apiResponse.getMessage());
        verify(productoService, times(1)).createProducto(productoDto);
    }

    @Test
    @DisplayName("Deberia actualizar un producto correctamente mediante su ID")
    void testUpdateProductoSucces() {
        // Preparacion
        doNothing().when(productoService).updateProducto(productoId, productoDto);

        // Ejecucion
        ResponseEntity<?> response = productoController.updateProducto(productoId, productoDto);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assert apiResponse != null;
        assertEquals("Producto actualizado correctamente", apiResponse.getMessage());
        assertTrue(apiResponse.isSuccess());
        verify(productoService, times(1)).updateProducto(productoId, productoDto);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFOundExcepcion")
    void testUpdateProductoNotFound() {
        // Preparacion
        doThrow(new ProductoNotFoundException("Producto no encontrado"))
                .when(productoService).updateProducto(productoId, productoDto);

        // Ejecucion
        ResponseEntity<?> response = productoController.updateProducto(productoId, productoDto);

        // Verificacion
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Producto no encontrado.", apiResponse.getMessage());
        assertFalse(apiResponse.isSuccess());
        verify(productoService, times(1)).updateProducto(productoId, productoDto);
    }

    @Test
    @DisplayName("Deberia eliminar un producto correctamente")
    void testDeleteProductoSuccess() {
        // Preparacion
        doNothing().when(productoService).deleteProducto(productoId);

        // Ejecucion
        ResponseEntity<?> response = productoController.deleteProducto(productoId);

        // Verificacion
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productoService, times(1)).deleteProducto(productoId);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFoundException")
    void testDeleteProductoNotFound() {
        // Preparacion
        doThrow(new ProductoNotFoundException("Producto no encontrado"))
                .when(productoService).deleteProducto(productoId);

        // Ejecucion
        ResponseEntity<?> response = productoController.deleteProducto(productoId);

        // Verificacion
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Producto no encontrado.", apiResponse.getMessage());
        assertFalse(apiResponse.isSuccess());
        verify(productoService, times(1)).deleteProducto(productoId);
    }

    @Test
    @DisplayName("Deberia obtener un producto mediante su ID correctamente")
    void testGetProductoSuccess() {
        // Preparacion
        when(productoService.getProductoById(productoId))
                .thenReturn(productoDto);

        // Ejecucion
        ResponseEntity<?> response = productoController.getProducto(productoId);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productoDto, response.getBody());
        verify(productoService, times(1)).getProductoById(productoId);
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFoundException")
    void testGetProductoNotFound() {
        // Preparacion
        when(productoService.getProductoById(productoId))
                .thenThrow(new ProductoNotFoundException("Producto no encontrado"));

        // Ejecucion
        ResponseEntity<?> response = productoController.getProducto(productoId);

        // Verificacion
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Producto no encontrado.", apiResponse.getMessage());
        assertFalse(apiResponse.isSuccess());

        verify(productoService, times(1)).getProductoById(productoId);
    }

    @Test
    @DisplayName("Deberia listar todos los productos correctamente")
    void testGetAllProductosSuccess() {
        // Preparacion
        List<ProductoDto> productos = List.of(productoDto);
        when(productoService.getAllProductos()).thenReturn(productos);

        // Ejecucion
        ResponseEntity<?> response = productoController.getAllProductos();

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productos, response.getBody());
        verify(productoService, times(1)).getAllProductos();
    }
}
