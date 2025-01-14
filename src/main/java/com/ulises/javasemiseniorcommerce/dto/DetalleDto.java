package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.ulises.javasemiseniorcommerce.model.DetalleModel}
 */
@Builder
@Data
@Schema(description = "Datos del detalle de un pedido.")
public class DetalleDto implements Serializable {
    Long id;

    @NotNull(message = "ID de producto obligatorio.")
    @Positive(message = "El ID del producto debe ser mayor que cero.")
    Long productoId;

    @NotNull(message = "Cantidad de producto obligatoria.")
    @Positive(message = "La cantidad de productos tiene que ser al menos 1.")
    Integer cantidad;

    @NotNull(message = "Precio obligatorio.")
    @Positive(message = "El precio no puede ser menor que cero.")
    Double precio;
}