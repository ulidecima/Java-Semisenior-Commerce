package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * @author ulide
 *
 * Clase DTO para recibir datos del cliente.
 */
@Builder
@Data
@Schema(description = "Datos necesarios para registrar un usuario.")
public class RegisterRequest {

    @NotBlank(message = "Nombre obligatorio.")
    private String nombre;

    @Email(message = "Debe proporcionar un correo electronico valido.")
    @NotBlank(message = "Correo electronico obligatorio.")
    private String email;

    @NotBlank(message = "La contrasenia es obligatoria.")
    @Size(min = 8, max = 20, message = "La contrasenia debe tener entre 8 y 20 caracteres.")
    private String password;
}
