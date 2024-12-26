package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

/**
 * @author ulide
 */
@Value
@Builder
public class ProductoCantidad {

    @NotBlank(message = "Nombre de producto obligatorio.")
    String nombreProducto;

    @NotNull(message = "Cantidad de producto obligatoria.")
    @PositiveOrZero(message = "La cantidad de productos no puede ser negativa.")
    Integer cantidad;
}
