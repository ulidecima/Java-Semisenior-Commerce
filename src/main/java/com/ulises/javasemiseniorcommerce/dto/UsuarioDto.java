package com.ulises.javasemiseniorcommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.ulises.javasemiseniorcommerce.model.UsuarioModel}
 */
@Value
@Builder
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