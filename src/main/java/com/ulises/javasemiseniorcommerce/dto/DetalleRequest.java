package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

/**
 * @author ulide
 */
@Value
@Builder
public class DetalleRequest {

    @NotNull(message = "ID de producto obligatorio.")
    @Positive(message = "El ID del producto debe ser mayor que cero.")
    Long productoId;

    @NotNull(message = "Cantidad de producto obligatoria.")
    @Positive(message = "La cantidad de productos tiene que ser al menos 1.")
    Integer cantidad;
}
