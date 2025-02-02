package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.ulises.javasemiseniorcommerce.model.ProductoModel}
 */
@Builder
@Data
@Schema(description = "Datos de un producto.")
public class ProductoDto implements Serializable {
    Long id;
    @NotBlank(message = "Nombre obligatorio.")
    @Size(min = 10, max = 50, message = "El nombre debe tener entre 30 y 50 caracteres.")
    String nombre;

    @NotBlank(message = "Descripcion obligatoria.")
    @Size(min = 10, max = 500, message = "La descripcion debe tener entre 30 y 500 caracteres.")
    String descripcion;

    @NotNull(message = "Precio obligatorio.")
    @Positive(message = "El precio tiene que ser mayor que cero.")
    Double precio;

    @NotNull(message = "Stock obligatorio.")
    @Positive(message = "El stock tiene que ser mayor que cero.")
    Integer stockDisponible;
}