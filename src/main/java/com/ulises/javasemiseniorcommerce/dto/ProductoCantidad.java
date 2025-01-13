package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

/**
 * @author ulide
 *
 * Clase DTO para transferir datos entre el cliente y el controlador y el servicio.
 */
@Value
@Builder
@Schema(description = "Datos de un producto que se muestran al listar los productos de un pedido.")
public class ProductoCantidad {

    @NotBlank(message = "Nombre de producto obligatorio.")
    String nombreProducto;

    @NotNull(message = "Cantidad de producto obligatoria.")
    @PositiveOrZero(message = "La cantidad de productos no puede ser negativa.")
    Integer cantidad;

    @NotNull(message = "Precio obligatorio.")
    @Positive(message = "El precio no puede ser menor o igual que cero.")
    Double precioUnidad;
}
