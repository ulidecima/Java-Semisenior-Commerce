package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author ulide
 *
 * Clase DTO para recibir datos del cliente.
 */
@Value
@Builder
@Schema(description = "Datos necesarios para crear un pedido.")
public class PedidoRequest {

    @NotBlank(message = "Nombre usuario obligatorio.")
    String username;

    @NotNull(message = "Lista de productos obligatoria.")
    @Size(min = 1, message = "El pedido debe tener al menos un producto.")
    @Valid
    List<DetalleRequest> detalles;
}
