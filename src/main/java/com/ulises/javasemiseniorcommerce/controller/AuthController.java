package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ulide
 */
@Tag(name = "Autenticacion", description = "Endpoinst de login y registro de usuario")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para iniciar sesion.
     *
     * @param request Datos del usuario registrado para iniciar sesion.
     * @return Token JWT si el logeo es correcto.
     */
    @Operation(
            summary = "Iniciar de sesion",
            description = "Autentica un usuario y devuelve un token JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario logeado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos."),
                    @ApiResponse(responseCode = "401", description = "Usuario no autorizado.")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token, "Usuario Autenticado"));
    }

    /**
     * Endpoint para registrar un usuario.
     *
     * @param request Datos para registrar el usuario nuevo.
     * @return Token JWT si el registro es correcto.
     */
    @Operation(
            summary = "Registrar usuario",
            description = "Registra un usuario nuevo y devuelve un token JWT.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos.")
            })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, "Usuario registrado."));
    }
}
