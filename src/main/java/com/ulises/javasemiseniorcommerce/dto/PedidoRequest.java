package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author ulide
 */
@Value
@Builder
public class PedidoRequest {

    @NotBlank(message = "Nombre usuario obligatorio.")
    String username;

    @NotNull(message = "Lista de productos obligatoria.")
    @Size(min = 1, message = "El pedido debe tener al menos un producto.")
    @Valid
    List<DetalleRequest> detalles;
}
