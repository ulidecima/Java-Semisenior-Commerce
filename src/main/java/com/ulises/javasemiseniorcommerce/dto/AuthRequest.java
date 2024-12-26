package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author ulide
 */
@Builder
@Data
public class AuthRequest {
    @Email(message = "Debe proporcionar un correo electronico valido.")
    @NotBlank(message = "Correo electronico obligatorio.")
    private String email;

    @NotBlank(message = "La contrasenia es obligatoria.")
    private String password;
}
