package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    /**
     * Obtiene un pedido mediante su ID.
     *
     * @param id ID del pedido.
     * @return Informacion del pedido.
     */
    @Operation(
            summary = "Obtener pedido",
            description = "Devuelve la informacion de un pedido mediante un ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido encontrado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "Pedido no encontrado.")})
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> getPedidoById(@PathVariable Long id) {
        PedidoDto pedido = pedidoService.getPedidoById(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Obtiene los productos de un pedido a partir del ID del pedido.
     *
     * @param id ID del pedido.
     * @return Lista de productos.
     */
    @Operation(
            summary = "Obtener productos de pedido",
            description = "Devuelve los productos que contiene un pedido mediante la ID del pedido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos encontrados."),
                    @ApiResponse(responseCode = "404", description = "Pedido no encontrado.")})
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoCantidad>> listProductos(@PathVariable Long id) {
        List<ProductoCantidad> productos = pedidoService.listProductos(id);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene el detalle completo de un pedido a partir del ID.
     *
     * @param id ID del pedido.
     * @return Informacion del pedido.
     */
    @Operation(
            summary = "Obtener detalle de pedido",
            description = "Devuelve el detalle del pedido (Productos, cantidad, precio de los productos y precio total).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Detalle del pedido encontrado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "Pedido no encontrado.")})
    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetallePedidoResponse> getDetalle(@PathVariable Long id) {
        DetallePedidoResponse pedido = pedidoService.getDetalleDePedido(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Crea un nuevo pedido.
     *
     * @param pedidoRequest Datos para crear del pedido.
     * @return Pedido creado.
     */
    @Operation(
            summary = "Crear pedido",
            description = "Crea un pedido.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos.")})
    @PostMapping
    public ResponseEntity<PedidoDto> createPedido(@Valid @RequestBody PedidoRequest pedidoRequest) {
        PedidoDto pedido = pedidoService.createPedido(pedidoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    /**
     * Elimina un pedido.
     *
     * @param id ID del pedido que se quiere eliminar.
     * @return Mensaje sin contenido.
     */
    @Operation(
            summary = "Eliminar pedido",
            description = "Elimina un pedido mediante un ID.",
            responses = {@ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente.")})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePedido(@PathVariable Long id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Obtiene todos los pedidos asociados a un email.
     *
     * @param email Email del usuario.
     * @return Lista de pedidos.
     */
    @Operation(summary = "Pedidos de usuario", description = "Devuelve todos los pedidos de un usuario registrado con el email proporcionado.")
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados exitosamente.")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    @GetMapping("/usuario/{email}")
    public ResponseEntity<Page<PedidoDto>> getPedidosByUsuario(
            @PathVariable String email,
            @Valid @PositiveOrZero(message = "El numero de la pagina tiene que ser positivo.") @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @Positive(message = "El tamanio de la muestra tiene que ser mayor que cero.") @RequestParam(required = false, defaultValue = "5") int size) {
        Page<PedidoDto> pedidos = pedidoService.getPedidosByMail(email, page, size);
        return ResponseEntity.ok(pedidos);
    }
}
