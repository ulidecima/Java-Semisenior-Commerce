package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.DetalleDto;
import com.ulises.javasemiseniorcommerce.dto.DetalleRequest;
import com.ulises.javasemiseniorcommerce.dto.PedidoDto;
import com.ulises.javasemiseniorcommerce.dto.PedidoRequest;
import com.ulises.javasemiseniorcommerce.exception.*;
import com.ulises.javasemiseniorcommerce.model.DetalleModel;
import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.PedidoRepository;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    private PedidoDto pedidoDto;
    private PedidoModel pedidoModel;
    private UsuarioModel usuarioModel;
    private ProductoModel productoModel;
    private DetalleDto detalleDto;
    private DetalleModel detalleModel;
    private PedidoRequest pedidoRequest;

    private final static String usuarioEmail = "test@mail.com";
    private final static Long usuarioId = 1L;

    private final static Long productoId = 1L;
    private final static Double precioProducto = 200.0;
    private final static Integer stockProducto = 10;
    private final static Integer cantidadProducto = 2;

    private final static Long pedidoId = 1L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuarioModel = UsuarioModel.builder()
                .id(usuarioId)
                .nombre("Usuario Test")
                .email(usuarioEmail)
                .password("paswrd")
                .habilitado(true)
                .build();

        productoModel = ProductoModel.builder()
                .id(productoId)
                .nombre("Producto Test")
                .descripcion("Descripcion")
                .stockDisponible(stockProducto)
                .precio(precioProducto)
                .build();

        detalleDto = DetalleDto.builder()
                .productoId(productoId)
                .cantidad(cantidadProducto)
                .build();

        detalleModel = DetalleModel.builder()
                .producto(productoModel)
                .cantidad(cantidadProducto)
                .build();

        pedidoModel = PedidoModel.builder()
                .id(pedidoId)
                .usuario(usuarioModel)
                .precio(cantidadProducto * precioProducto)
                .detalles(List.of(detalleModel))
                .fechaDeCreacion(LocalDateTime.now())
                .build();

        pedidoDto = PedidoDto.builder()
                .usuarioId(usuarioId)
                .username(usuarioEmail)
                .detalles(List.of(detalleDto))
                .build();

        pedidoRequest = PedidoRequest.builder()
                .username(usuarioEmail)
                .detalles(Arrays.asList(
                        DetalleRequest.builder()
                                .productoId(productoId)
                                .cantidad(cantidadProducto)
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("Deberia crear un pedido correctamente")
    void testCreateProductoSuccess() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.of(usuarioModel));

        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoModel));

        when(pedidoRepository.save(any(PedidoModel.class)))
                .thenReturn(pedidoModel);

        // Ejecucion
        PedidoDto resultado = pedidoService.createPedido(pedidoRequest);

        // Verificacion
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(cantidadProducto * precioProducto, resultado.getPrecio());
        verify(pedidoRepository, times(2)).save(any(PedidoModel.class));
        verify(productoRepository, times(1)).save(productoModel);
    }

    @Test
    @DisplayName("Deberia lanzar una UserNotFounException")
    void testCreatePedidoUserNotFound() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.empty());

        // Ejecucion
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            pedidoService.createPedido(pedidoRequest);
        });

        // Verificacion
        assertEquals("Usuario no encontrado con email: " + usuarioEmail, exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(usuarioEmail);
    }

    @Test
    @DisplayName("Deberia lanzar una PedidoSinProductosException si el pedido no contiene productos")
    void testCreatePedidoSinProductos() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.of(usuarioModel));

        PedidoRequest pedidoVacio = PedidoRequest.builder()
                .username(usuarioEmail)
                .detalles(List.of()) // Lista vacia
                .build();

        // Eejcucion
        RuntimeException exception = assertThrows(PedidoSinProductosException.class, () -> {
            pedidoService.createPedido(pedidoVacio);
        });

        // Verificacion
        assertEquals("El pedido debe contener al menos un producto.", exception.getMessage());
        verify(pedidoRepository, never()).save(any(PedidoModel.class));
    }

    @Test
    @DisplayName("Deberia lanzar una ProductoNotFoundException")
    void testCreatePedidoNotFound() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.of(usuarioModel));

        when(productoRepository.findById(productoId))
                .thenReturn(Optional.empty());

        // Ejecucion
        ProductoNotFoundException exception = assertThrows(ProductoNotFoundException.class, () -> {
            pedidoService.createPedido(pedidoRequest);
        });

        // Verificacion
        assertEquals("No se ha encontrado el producto", exception.getMessage());
        verify(productoRepository, times(1)).findById(productoId);
    }

    @Test
    @DisplayName("Deberia lanzar una StockInsuficienteException")
    void testCreatePedidoStockInsuficiente() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.of(usuarioModel));

        ProductoModel productoStockInsuficiente = ProductoModel.builder()
                .id(productoId)
                .nombre("Producto Test - Stock insuficiente")
                .descripcion("Producto con stock insuficiente")
                .stockDisponible(1)
                .precio(precioProducto)
                .build();
        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoStockInsuficiente));

        StockInsuficienteException exception = assertThrows(StockInsuficienteException.class, () -> {
            pedidoService.createPedido(pedidoRequest);
        });

        assertEquals("Stock insuficiente para el producto: " + productoStockInsuficiente.getNombre(), exception.getMessage());
        verify(productoRepository, never()).save(productoStockInsuficiente);
    }

    @Test
    @DisplayName("Deberia obtener un pedido correctamente mediante su ID")
    void testGetProductoByIdSuccess() {
        // Preparacion
        when(usuarioRepository.findByEmail(usuarioEmail))
                .thenReturn(Optional.of(usuarioModel));

        when(productoRepository.findById(productoId))
                .thenReturn(Optional.of(productoModel));

        when(pedidoRepository.findById(pedidoId))
                .thenReturn(Optional.of(pedidoModel));

        // Ejecucion
        PedidoDto resultado = pedidoService.getPedidoById(pedidoId);

        // Verificacion
        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getId());
        assertEquals(usuarioId, resultado.getUsuarioId());
        verify(pedidoRepository, times(1)).findById(pedidoId);
    }

    @Test
    @DisplayName("Deberia lanzar una PedidoNotFoundException")
    void testGetPedidoByIdNotFound() {
        when(pedidoRepository.findById(pedidoId))
                .thenReturn(Optional.empty());

        PedidoNotFoundException exception = assertThrows(PedidoNotFoundException.class, () -> {
            pedidoService.getPedidoById(pedidoId);
        });

        assertEquals("Pedido no encontrado con ID: " + pedidoId, exception.getMessage());
        verify(pedidoRepository, times(1)).findById(pedidoId);
    }
}
