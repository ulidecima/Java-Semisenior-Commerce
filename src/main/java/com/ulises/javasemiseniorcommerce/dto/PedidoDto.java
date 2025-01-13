package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.ulises.javasemiseniorcommerce.model.PedidoModel}
 */
@Value
@Builder
@Setter
@Schema(description = "Datos de un pedido.")
public class PedidoDto implements Serializable {
    Long id;

    @NotNull(message = "ID de usuario obligatorio.")
    @Positive(message = "El ID del usuario debe ser mayor que cero.")
    Long usuarioId;

    @NotBlank(message = "Nombre usuario obligatorio.")
    String username;

    @NotNull(message = "Lista de productos obligatoria.")
    @Size(min = 1, message = "El pedido debe tener al menos un producto.")
    @Valid
    List<DetalleDto> detalles;

    @NotNull(message = "Precio total obligatorio.")
    @PositiveOrZero(message = "El precio total debe ser igual o mayor que cero.")
    Double precio;

    @NotNull(message = "Fecha de creacion obligatoria.")
    LocalDateTime fechaDeCreacion;
}