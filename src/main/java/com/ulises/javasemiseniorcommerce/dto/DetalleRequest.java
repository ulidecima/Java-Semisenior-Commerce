package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

/**
 * @author ulide
 *
 * Clase DTO para recibir datos del cliente.
 */
@Value
@Builder
@Schema(description = "Datos necesarios para crear un detalle de un pedido. Se agrupan en una lista que se indexa un PedidoRequest")
public class DetalleRequest {

    @NotNull(message = "ID de producto obligatorio.")
    @Positive(message = "El ID del producto debe ser mayor que cero.")
    Long productoId;

    @NotNull(message = "Cantidad de producto obligatoria.")
    @Positive(message = "La cantidad de productos tiene que ser al menos 1.")
    Integer cantidad;
}
