package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author ulide
 *
 * Clase DTO para devolver datos al cliente.
 */
@Builder
@Data
@Schema(description = "Respuesta de logeo exitoso con un mensaje y token JWT")
public class AuthResponse {
    private String token;
    private String message;
}
