package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @author ulide
 *
 * Clase DTO para devolver datos al cliente.
 */
@Builder
@Data
@Getter
@AllArgsConstructor
@Schema(description = "Respuesta de logeo exitoso con un mensaje y token JWT")
public class AuthResponse {
    private String token;
    private String message;
}
