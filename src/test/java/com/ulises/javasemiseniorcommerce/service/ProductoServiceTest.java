package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.dto.ProductoRequest;
import com.ulises.javasemiseniorcommerce.exception.notfound.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestDataFactory;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @InjectMocks
    private ProductoService productoService;

    @Mock
    private ProductoRepository productoRepository;

    @Nested
    @DisplayName("ProductoService Create Tests")
    class productoServiceCreateTests {
        @Test
        @DisplayName("Deberia crear un producto correctamente")
        void testCreateProductoSuccess() {
            // Preparacion
            ProductoModel producto = TestDataFactory.crearProductoModel();
            ProductoRequest productoRequest = TestDataFactory.crearProductoRequest();

            when(productoRepository.save(any(ProductoModel.class)))
                    .thenReturn(producto);

            // Ejecucion
            ProductoDto result = productoService.createProducto(productoRequest);

            // Verificacion
            assertEquals(productoRequest.getNombre(), result.getNombre());
            assertEquals(productoRequest.getDescripcion(), result.getDescripcion());
            assertEquals(productoRequest.getStockDisponible(), result.getStockDisponible());
            assertEquals(productoRequest.getPrecio(), result.getPrecio());
            verify(productoRepository, times(1)).save(any(ProductoModel.class));
        }
    }

    @Nested
    @DisplayName("ProductoService Get Tests")
    class productoServiceGetTests {
        @Test
        @DisplayName("Deberia retornar la informacion de un producto correctamente")
        void testGetProductoSuccess() {
            // Preparacion
            ProductoModel productoModel = TestDataFactory.crearProductoModel();

            when(productoRepository.findById(productoModel.getId()))
                    .thenReturn(Optional.of(productoModel));

            // Ejecucion
            ProductoDto result = productoService.getProductoById(productoModel.getId());

            // Verificacion
            assertNotNull(result);
            assertEquals(productoModel.getNombre(), result.getNombre());
            assertEquals(productoModel.getDescripcion(), result.getDescripcion());
            assertEquals(productoModel.getStockDisponible(), result.getStockDisponible());
            assertEquals(productoModel.getPrecio(), result.getPrecio());
            verify(productoRepository, times(1)).findById(productoModel.getId());
        }

        @Test
        @DisplayName("Deberia lanzar una ProductoNotFOundException")
        void testGetProductoNotFound() {
            // Preparacion
            Long productoId = 1L;
            when(productoRepository.findById(productoId))
                    .thenReturn(Optional.empty());

            // Ejecucion
            ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class,
                    () -> productoService.getProductoById(productoId));

            // Verificacion
            assertEquals("Producto no encontrado con ID: " + productoId, exception.getMessage());
            verify(productoRepository, times(1)).findById(productoId);
        }

        @Test
        @DisplayName("Deberia retornar todos los productos de la base de datos correctamente")
        void testGetAllProductos() {
            // Preparacion
            Pageable pageable = PageRequest.of(0, 5);
            ProductoModel productoModel = TestDataFactory.crearProductoModel();
            List<ProductoModel> productos = List.of(productoModel);
            Page<ProductoModel> pageProductos = new PageImpl<>(productos);

            when(productoRepository.findAll(pageable))
                    .thenReturn(pageProductos);

            // Ejecucion
            Page<ProductoDto> resultado = productoService.getAllProductos(null, null, 0, 5);

            // Verificacion
            assertNotNull(resultado);
            assertEquals(productos.size(), resultado.getContent().size());
            for (int i = 0; i < resultado.getContent().size(); i++) {
                assertEquals(productos.get(i).getNombre(), resultado.getContent().get(i).getNombre());
                assertEquals(productos.get(i).getDescripcion(), resultado.getContent().get(i).getDescripcion());
                assertEquals(productos.get(i).getStockDisponible(), resultado.getContent().get(i).getStockDisponible());
                assertEquals(productos.get(i).getPrecio(), resultado.getContent().get(i).getPrecio());
            }
            verify(productoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deberia retornar una lista vacia")
        void testGetAllProductosEmpty() {
            // Preparacion
            Pageable pageable = PageRequest.of(0, 5);
            when(productoRepository.findAll(pageable))
                    .thenReturn(Page.empty());

            // Ejecucion
            Page<ProductoDto> resultado = productoService.getAllProductos(null, null,0, 5);

            // Verificacion
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(productoRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("ProductoService Update Tests")
    class ProductoServiceUpdateTests {
        @Test
        @DisplayName("Deberia actualizar los datos de un producto correctamente")
        void testUpdateProductoSuccess() {
            // Preparacion
            Long productoId = 1L;
            ProductoRequest productoRequest = TestDataFactory.crearProductoRequest();
            ProductoModel productoModel = TestDataFactory.crearProductoModel();

            when(productoRepository.findById(productoId))
                    .thenReturn(Optional.of(productoModel));

            // Ejecucion
            ProductoDto result = productoService.updateProducto(productoId, productoRequest);

            // Verificacion
            assertEquals(result.getNombre(), productoModel.getNombre());
            assertEquals(result.getDescripcion(), productoModel.getDescripcion());
            assertEquals(result.getStockDisponible(), productoModel.getStockDisponible());
            assertEquals(result.getPrecio(), productoModel.getPrecio());
            verify(productoRepository, times(1)).save(productoModel);
        }

        @Test
        @DisplayName("Deberia lanzar una ProductoNotFoundException")
        void testProductoNotFound() {
            // Preparacion
            ProductoRequest productoRequest = TestDataFactory.crearProductoRequest();
            Long productoId = 1L;
            when(productoRepository.findById(productoId))
                    .thenReturn(Optional.empty());

            // Ejecucion
            ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class,
                    () -> productoService.updateProducto(1L, productoRequest));

            // Verificacion
            assertEquals("Producto no encontrado con ID: " + productoId, exception.getMessage());
            verify(productoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("ProductoService Delete Tests")
    class productoServiceDeleteTests {
        @Test
        @DisplayName("Deberia eliminar un producto correctamente")
        void testDeleteProductoSUccess() {
            // Preparacion
            Long productoId = 1L;
            ProductoModel productoModel = TestDataFactory.crearProductoModel();
            when(productoRepository.findById(productoId))
                    .thenReturn(Optional.of(productoModel));

            // Ejecucion
            productoService.deleteProducto(productoId);

            // Verificacion
            verify(productoRepository, times(1)).delete(productoModel);
        }
    }
}
