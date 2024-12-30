package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.exception.*;
import com.ulises.javasemiseniorcommerce.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ulide
 */
@Tag(name = "Pedidos", description = "Endpoint para gestionar pedidos.")
@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    /**
     * Obtiene un pedido mediante su ID
     * 
     * @param id ID del pedido
     * @return Informacion del pedido
     */
    @Operation(summary = "Obtener pedido", description = "Devuelve la informacion de un pedido mediante un ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPedidoById(@PathVariable Long id) {
        logger.info("GET_PEDIDO_REQUEST: ID recibido: {}", id);
        
        try {
            PedidoDto pedido = pedidoService.getPedidoById(id);
            logger.info("GET_PEDIDO_SUCCESS: Pedido obtenido con ID: {}", id);
            return ResponseEntity.ok(pedido);
        } catch (PedidoNotFoundException e) {
            logger.warn("GET_PEDIDO_ERROR: Pedido no encontrado con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Pedido no encontrado.", false));
        } catch (Exception e) {
            logger.error("GET_PEDIDO_ERROR: Hubo un error inseperado al obtener pedido con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al obtener el pedido", false));
        }
    }

    /**
     * Obtiene los productos de un pedido a partir del ID del pedido
     *
     * @param id ID del pedido
     * @return Lista de productos
     */
    @Operation(summary = "Obtener productos de pedido", description = "Devuelve los productos que contiene un pedido mediante la ID del pedido.")
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> listProductos(@PathVariable Long id) {
        logger.info("GET_PRODUCTOS_PEDIDO_REQUEST: ID recibido: {}", id);
        try {
            List<DetalleDto> productos = pedidoService.listProductos(id);
            logger.info("GET_PRODUCTOS_PEDIDO_SUCCESS: El pedido con ID {} contiene {} productos", id, productos.size());
            return ResponseEntity.ok(productos);
        } catch (PedidoNotFoundException e) {
            logger.warn("GET_PRODUCTOS_PEDIDO_ERROR: Pedido no encontrado con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Pedido no encontrado.", false));
        } catch (Exception e) {
            logger.error("GET_PRODUCTOS_PEDIDO_ERROR: Error inesperado al listar los productos del pedido con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado", false));
        }
    }

    /**
     * Obtiene el detalle completo de un pedido a partir del ID
     * @param id ID del pedido
     * @return Informacion del pedido
     */
    @Operation(summary = "Obtener detalle de pedido", description = "Devuelve el detalle del pedido (Productos, cantidad, precio de los productos y precio total).")
    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> getDetalle(@PathVariable Long id) {
        logger.info("GET_DETALLE_PEDIDO_REQUEST: ID recibido: {}", id);
        
        try {
            DetallePedidoResponse pedido = pedidoService.getDetalleDePedido(id);
            logger.info("GET_DETALLE_PEDIDO_SUCCESS: Detalle obtenido para el pedido con ID: {}", id);
            return ResponseEntity.ok(pedido);
        } catch (PedidoNotFoundException e) {
            logger.warn("GET_DETALLE_PEDIDO_ERROR: Pedido no encontrado con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Pedido no encontrado", false));
        } catch (Exception e) {
            logger.error("GET_DETALLE_PEDIDO_ERROR: hubo un error inesperado al obtener el detalle del pedido con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al obtener el detalle del pedido.", false));
        }
    }

    /**
     * Crea un nuevo pedido
     *
     * @param pedidoRequest Datos para crear del pedido
     * @return Pedido creado
     */
    @Operation(summary = "Crear pedido", description = "Crea un pedido.")
    @PostMapping
    public ResponseEntity<ApiResponse> createPedido(@Valid @RequestBody PedidoRequest pedidoRequest) {
        logger.info("CREATE_PEDIDO_REQUES: Datos recibidos: {}", pedidoRequest);
        try {
            PedidoDto pedido = pedidoService.createPedido(pedidoRequest);
            logger.info("CREATE_PEDIDO_SUCCESS: Pedido creado con ID: {}", pedido.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Pedido creado correctamente.", true));
        } catch (UserNotFoundException | ProductoNotFoundException |
                 StockInsuficienteException | PedidoSinProductosException e) {
            logger.warn("CREATE_PEDIDO_ERROR: Hubo un error de validacion al crear el pedido. Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Datos invalidos", false));
        } catch (Exception e) {
            logger.error("CREATE_PEDIDO_ERROR: Hubo un error al crear el pedido, Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado al crear pedido", false));
        }
    }

    /**
     * Elimina un pedido
     * 
     * @param id ID del pedido que se quiere eliminar
     * @return Mensaje de exito
     */
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido mediante un ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePedido(@PathVariable Long id) {
        logger.info("DELETE_PEDIDO_REQUEST: ID recibido: {}", id);
        try {
            pedidoService.deletePedido(id);
            logger.info("DELETE_PEDIDO_SUCCESS: Pedido eliminado con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PedidoNotFoundException e) {
            logger.warn("DELETE_PEDIDO_ERROR: Pedido no encontrado con ID: {}. Error {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Pedido no encontrado", false));
        } catch (Exception e) {
            logger.error("DELETE_PEDIDO_ERROR: Hubo un error inesperado al eliminar el pedido con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado al eliminar el pedido", false));
        }
    }

    /**
     * Obtiene todos los pedidos asociados a un email
     * @param email Email del usuario
     * @return Lista de pedidos
     */
    @Operation(summary = "Pedidos de usuario", description = "Devuelve todos los pedidos de un usuario registrado con el email proporcionado.")
    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> getPedidosByUsuario(
            @PathVariable String email,
            @RequestParam int page,
            @RequestParam int size
    ) {
        logger.info("GET_PEDIDOS_USUARIO_REQUEST: email recibido: {}", email);
        try {
            List<PedidoDto> pedidos = pedidoService.getPedidosByMail(email, page, size);
            logger.info("GET_PEDIDOS_USUARIO_SUCCESS: Se encontraron {} pedidos asociados al email {}", pedidos.size(), email);
            return ResponseEntity.ok(pedidos);
        } catch (UserNotFoundException e) {
            logger.warn("GET_PEDIDOS_USUARIO_ERROR: Usuario no encontrado con email: {}. Error {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Usuario no encontrado", false));
        } catch (Exception e) {
            logger.error("GET_PEDIDOS_USUARIO_ERROR: Error inesperado al obtener los pedidos del usuario con email: {}. Error: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado", false));
        }
    }
}
