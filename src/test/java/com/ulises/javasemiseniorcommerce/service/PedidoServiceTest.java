package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.PedidoDto;
import com.ulises.javasemiseniorcommerce.dto.PedidoRequest;
import com.ulises.javasemiseniorcommerce.exception.badrquest.PedidoSinProductosException;
import com.ulises.javasemiseniorcommerce.exception.badrquest.StockInsuficienteException;
import com.ulises.javasemiseniorcommerce.exception.notfound.PedidoNotFoundException;
import com.ulises.javasemiseniorcommerce.exception.notfound.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.exception.notfound.UserNotFoundException;
import com.ulises.javasemiseniorcommerce.model.DetalleModel;
import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.PedidoRepository;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestDataFactory;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Nested
    @DisplayName("PedidoService Get Tests")
    class PedidoServiceGetTests {
        @Test
        @DisplayName("Deberia obtener un pedido correctamente mediante su ID")
        void testGetProductoByIdSuccess() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            ProductoModel producto = TestDataFactory.crearProductoModel();
            DetalleModel detalle = DetalleModel.builder()
                    .producto(producto)
                    .cantidad(2)
                    .build();
            PedidoModel pedido = TestDataFactory.crearPedido(usuario, List.of(detalle));

            when(pedidoRepository.findById(pedido.getId()))
                    .thenReturn(Optional.of(pedido));

            // Ejecucion
            PedidoDto resultado = pedidoService.getPedidoById(pedido.getId());

            // Verificacion
            assertNotNull(resultado);
            assertEquals(pedido.getId(), resultado.getId());
            assertEquals(usuario.getId(), resultado.getUsuarioId());
            verify(pedidoRepository, times(1)).findById(pedido.getId());
        }

        @Test
        @DisplayName("Deberia lanzar una PedidoNotFoundException")
        void testGetPedidoByIdNotFound() {
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            ProductoModel producto = TestDataFactory.crearProductoModel();
            DetalleModel detalle = DetalleModel.builder()
                    .producto(producto)
                    .cantidad(2)
                    .build();
            PedidoModel pedido = TestDataFactory.crearPedido(usuario, List.of(detalle));
            when(pedidoRepository.findById(pedido.getId()))
                    .thenReturn(Optional.empty());

            PedidoNotFoundException exception = assertThrows(PedidoNotFoundException.class,
                    () -> pedidoService.getPedidoById(pedido.getId()));

            assertEquals("Pedido no encontrado con ID: " + pedido.getId(), exception.getMessage());
            verify(pedidoRepository, times(1)).findById(pedido.getId());
        }
    }

    @Nested
    @DisplayName("PedidoService Create Tests")
    class PedidoServiceCreateTests {
        @Test
        @DisplayName("Deberia crear un pedido correctamente")
        void testCreateProductoSuccess() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            PedidoRequest pedidoRequest = TestDataFactory.crearPedidoRequest();
            ProductoModel producto = TestDataFactory.crearProductoModel();
            DetalleModel detalle = DetalleModel.builder()
                    .producto(producto)
                    .cantidad(2)
                    .build();
            PedidoModel pedido = TestDataFactory.crearPedido(usuario, List.of(detalle));

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));
            when(productoRepository.findById(producto.getId()))
                    .thenReturn(Optional.of(producto));
            when(pedidoRepository.save(any()))
                    .thenReturn(pedido);

            // Ejecucion
            PedidoDto resultado = pedidoService.createPedido(pedidoRequest);

            // Verificacion
            assertNotNull(resultado, "El resultado no debe ser nulo.");
            assertEquals(usuario.getId(), resultado.getUsuarioId());
            assertEquals(pedido.getPrecio(), resultado.getPrecio());
            verify(pedidoRepository, times(2)).save(any(PedidoModel.class));
            verify(productoRepository, times(1)).save(producto);
        }

        @Test
        @DisplayName("Deberia lanzar una UserNotFounException")
        void testCreatePedidoUserNotFound() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            PedidoRequest pedidoRequest = TestDataFactory.crearPedidoRequest();

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.empty());

            // Ejecucion
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () -> pedidoService.createPedido(pedidoRequest));

            // Verificacion
            assertEquals("Usuario no encontrado con email: " + usuario.getEmail(), exception.getMessage());
            verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        }

        @Test
        @DisplayName("Deberia lanzar una PedidoSinProductosException si el pedido no contiene productos")
        void testCreatePedidoSinProductos() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            PedidoRequest pedidoVacio = PedidoRequest.builder()
                    .username(usuario.getEmail())
                    .detalles(List.of()) // Lista de productos vacia
                    .build();

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));

            // Eejcucion
            RuntimeException exception = assertThrows(PedidoSinProductosException.class,
                    () -> pedidoService.createPedido(pedidoVacio));

            // Verificacion
            assertEquals("El pedido debe contener al menos un producto.", exception.getMessage());
            verify(pedidoRepository, never()).save(any(PedidoModel.class));
        }

        @Test
        @DisplayName("Deberia lanzar una ProductoNotFoundException")
        void testCreatePedidoNotFound() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            PedidoRequest pedidoRequest = TestDataFactory.crearPedidoRequest();
            ProductoModel producto = TestDataFactory.crearProductoModel();

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));

            when(productoRepository.findById(producto.getId()))
                    .thenReturn(Optional.empty());

            // Ejecucion
            ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class,
                    () -> pedidoService.createPedido(pedidoRequest));

            // Verificacion
            assertEquals("No se ha encontrado el producto", exception.getMessage());
            verify(productoRepository, times(1)).findById(producto.getId());
        }

        @Test
        @DisplayName("Deberia lanzar una StockInsuficienteException")
        void testCreatePedidoStockInsuficiente() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            PedidoRequest pedidoRequest = TestDataFactory.crearPedidoRequest();
            ProductoModel productoStockInsuficiente = ProductoModel.builder()
                    .id(1L)
                    .nombre("Producto Test - Stock insuficiente")
                    .descripcion("Producto con stock insuficiente")
                    .stockDisponible(1)
                    .precio(100.0)
                    .build();

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));

            when(productoRepository.findById(productoStockInsuficiente.getId()))
                    .thenReturn(Optional.of(productoStockInsuficiente));

            StockInsuficienteException exception = assertThrows(StockInsuficienteException.class,
                    () -> pedidoService.createPedido(pedidoRequest));

            assertEquals("Stock insuficiente para el producto: " + productoStockInsuficiente.getNombre(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("PedidoService Delete Tests")
    class PedidoServiceDeleteTests {
        @Test
        @DisplayName("Deberia eliminar un pedido correctamente.")
        void testDeletePedidoSuccess() {
            // Preparacion
            PedidoModel pedido = TestDataFactory.crearPedido();

            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

            // Ejecucion
            pedidoService.deletePedido(pedido.getId());

            // Verificacion
            verify(pedidoRepository, times(1)).findById(pedido.getId());
            verify(pedidoRepository, times(1)).delete(pedido);
        }
    }
}
