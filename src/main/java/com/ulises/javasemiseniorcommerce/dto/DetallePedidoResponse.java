package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ulide
 */
@Value
@Builder
public class DetallePedidoResponse {

    @NotBlank(message = "Nombre usuario obligatorio.")
    String username;

    @NotNull(message = "Lista de productos obligatoria.")
    @Size(min = 1, message = "El pedido debe tener al menos un producto.")
    @Valid
    List<ProductoCantidad> productos;

    @NotNull(message = "Precio total obligatorio.")
    @PositiveOrZero(message = "El precio total debe ser igual o mayor que cero.")
    Double precioTotal;

    @NotNull(message = "Fecha de creacion obligatoria.")
    LocalDateTime fechaDeCreacion;
}
