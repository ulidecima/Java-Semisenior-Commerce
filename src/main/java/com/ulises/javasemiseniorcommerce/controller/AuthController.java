package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.exception.AuthenticationFailedException;
import com.ulises.javasemiseniorcommerce.exception.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Endpoint para iniciar sesion
     *
     * @param request
     * @return Token JWT si el logeo es correcto
     */
    @Operation(summary = "Iniciar de sesion", description = "Autentica un usuario y devuelve un token JWT.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        logger.info("LOGIN_REQUEST: Intentando autenticar usuario.");
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Usuario Autenticado"));
        } catch (AuthenticationFailedException e) {
            logger.error("LOGIN_FAILED: Error al autenticar usuario {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Credenciales invalidas.", false));
        }
    }

    /**
     * Endpoint para registrar un usuario
     *
     * @param request
     * @return Token JWT si el registro es correcto
     */
    @Operation(summary = "Registrar usuario", description = "Registra un usuario nuevo y devuelve un token JWT.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("REGISTER_REQUEST: Intentando registrar usuario.");
        try {
            String token = authService.register(request.getNombre(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Usuario registrado."));
        } catch (EmailAlreadyExistsException e) {
            logger.warn("REGISTER_FAILED: El correo electronico ya esta registrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("El correo electronico ya esta en uso.", false));
        } catch (Exception e) {
            logger.error("REGISTER_FAILED: Error al registrar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al registrar usuario.", false));
        }
    }
}
