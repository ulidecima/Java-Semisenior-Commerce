package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author ulide
 *
 * Clase DTO para recibir datos del cliente.
 */
@Builder
@Data
@Schema(description = "Datos necesarios para autenticacion de un usuario.")
public class AuthRequest {
    @Email(message = "Debe proporcionar un correo electronico valido.")
    @NotBlank(message = "Correo electronico obligatorio.")
    private String email;

    @NotBlank(message = "La contrasenia es obligatoria.")
    private String password;
}
