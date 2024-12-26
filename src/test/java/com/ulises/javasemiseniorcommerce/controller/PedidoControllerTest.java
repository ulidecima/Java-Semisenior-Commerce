package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.exception.PedidoNotFoundException;
import com.ulises.javasemiseniorcommerce.service.PedidoService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class PedidoControllerTest {

    @InjectMocks
    private PedidoController pedidoController;

    @Mock
    private PedidoService pedidoService;

    private PedidoDto pedidoDto;
    private DetalleDto detalleDto;
    private DetallePedidoResponse detallePedidoResponse;
    private PedidoRequest pedidoRequest;

    private static final Long pedidoId = 1L;

    private static final Long usuarioId = 1L;
    private static final String usuarioEmail = "test@mail.com";

    private static final Long productoId = 1L;
    private static final Integer cantidadProducto = 2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        pedidoDto = PedidoDto.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .username(usuarioEmail)
                .detalles(Arrays.asList(
                        DetalleDto.builder()
                                .productoId(productoId)
                                .cantidad(cantidadProducto)
                                .build()
                ))
                .build();

        detalleDto = DetalleDto.builder()
                .productoId(productoId)
                .cantidad(cantidadProducto)
                .build();

        detallePedidoResponse = DetallePedidoResponse.builder()
                .productos(Arrays.asList(
                        ProductoCantidad.builder().nombreProducto("Producto Test").cantidad(cantidadProducto).build()
                ))
                .precioTotal(cantidadProducto * 100.0)
                .username(usuarioEmail)
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
    @DisplayName("Deberia obtener un pedido mediante su ID correctamente")
    void testGetPedidoByIdSuccess() {
        when(pedidoService.getPedidoById(pedidoId))
                .thenReturn(pedidoDto);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.getPedidoById(pedidoId);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoDto, response.getBody());
        verify(pedidoService, times(1)).getPedidoById(pedidoId);
    }

    @Test
    @DisplayName("Deberia lanzar PedidoNotFOundException")
    void testGetPedidoByIdNotFound() {
        // Preparacion
        when(pedidoService.getPedidoById(pedidoId))
                .thenThrow(new PedidoNotFoundException("Pedido no encontrado"));

        // Ejecucion
        try {
            pedidoController.getPedidoById(pedidoId);
        } catch (PedidoNotFoundException e) {
            assertEquals("Pedido no encontrado", e.getMessage());
        }

        verify(pedidoService, times(1)).getPedidoById(pedidoId);
    }

    @Test
    @DisplayName("Deberia listar productos de un pedido correctamente")
    void testListProductosSuccess() {
        // Preparacion
        List<DetalleDto> detalles = Arrays.asList(detalleDto);
        when(pedidoService.listProductos(pedidoId))
                .thenReturn(detalles);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.listProductos(pedidoId);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detalles, response.getBody());
        verify(pedidoService, times(1)).listProductos(pedidoId);
    }

    @Test
    @DisplayName("Deberia obtener el detalle de un pedido correctamente")
    void testGetDetalleSuccess() {
        // Preparacion
        when(pedidoService.getDetalleDePedido(pedidoId))
                .thenReturn(detallePedidoResponse);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.getDetalle(pedidoId);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoDto, response.getBody());
        verify(pedidoService, times(1)).getDetalleDePedido(pedidoId);
    }

    @Test
    @DisplayName("Deberia lanzar una PedidoNotFoundException")
    void testGetDetalleNotFound() {
        // Preparacion
        when(pedidoService.getDetalleDePedido(pedidoId))
                .thenThrow(new PedidoNotFoundException("Pedido no encontrado"));
        
        // Ejecucion
        try {
            pedidoController.getDetalle(pedidoId);
        } catch (PedidoNotFoundException e) {
            assertEquals("Pedido no encontrado", e.getMessage());
        }

        verify(pedidoService, times(1)).getDetalleDePedido(pedidoId);
    }

    @Test
    @DisplayName("Deberia crear un pedido correctamente")
    void testCreatePedidoSuccess() {
        // Preparacion
        when(pedidoService.createPedido(pedidoRequest))
                .thenReturn(pedidoDto);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.createPedido(pedidoRequest);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidoDto, response.getBody());
        verify(pedidoService, times(1)).createPedido(pedidoRequest);
    }

    @Test
    @DisplayName("Deberia eliminar un pedido correctamente")
    void testDeletePedidoSuccess() {
        // Preparacion
        doNothing().when(pedidoService).deletePedido(pedidoId);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.deletePedido(pedidoId);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido eliminado correctamente.", response.getBody());
        verify(pedidoService, times(1)).deletePedido(pedidoId);
    }

    @Test
    @DisplayName("Deberia lanzar PedidoNotFoundException")
    void testDeletePedidoNotFound() {
        // Preparacion
        doThrow(new PedidoNotFoundException("Pedido no encontrado"))
                .when(pedidoService).deletePedido(pedidoId);

        // Ejecucion y Verificacion
        try {
            pedidoController.deletePedido(pedidoId);
        } catch (PedidoNotFoundException e) {
            assertEquals("Pedido no encontrado", e.getMessage());
        }

        verify(pedidoService, times(1)).deletePedido(pedidoId);
    }

    @Test
    @DisplayName("Deberia obtener todos los pedidos de un usuario mediante su Email correctamente")
    void testGetAllProductosByUsuarioSuccess() {
        // Preparacion
        List<PedidoDto> pedidos = Arrays.asList(pedidoDto);
        when(pedidoService.getPedidosByMail(usuarioEmail))
                .thenReturn(pedidos);

        // Ejecucion
        ResponseEntity<?> response = pedidoController.getPedidosByUsuario(usuarioEmail);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
        verify(pedidoService, times(1)).getPedidosByMail(usuarioEmail);
    }
}
