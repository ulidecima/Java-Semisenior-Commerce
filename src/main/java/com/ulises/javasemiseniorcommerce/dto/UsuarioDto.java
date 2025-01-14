package com.ulises.javasemiseniorcommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.ulises.javasemiseniorcommerce.model.UsuarioModel}
 */
@Builder
@Data
@Schema(description = "Datos del usuario.")
public class UsuarioDto implements Serializable {

    Long id;

    @NotBlank(message = "Nombre obligatorio.")
    private String nombre;

    @Email(message = "Debe proporcionar un correo electronico valido.")
    @NotBlank(message = "Correo electronico obligatorio.")
    private String email;

    @NotBlank(message = "La contrasenia es obligatoria.")
    private String password;

    boolean habilitado;
}