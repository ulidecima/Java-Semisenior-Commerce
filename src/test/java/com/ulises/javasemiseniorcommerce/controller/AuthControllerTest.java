package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ApiResponse;
import com.ulises.javasemiseniorcommerce.dto.AuthRequest;
import com.ulises.javasemiseniorcommerce.dto.AuthResponse;
import com.ulises.javasemiseniorcommerce.dto.RegisterRequest;
import com.ulises.javasemiseniorcommerce.exception.AuthenticationFailedException;
import com.ulises.javasemiseniorcommerce.exception.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.service.AuthService;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private AuthRequest authRequest;
    private RegisterRequest registerRequest;

    private final static String usuarioEmail = "test@mail.com";
    private final static String usuarioNombre = "Usuario Test";
    private final static String password = "psswrd";
    private final static String token = "mocked-jwt-token";

    @BeforeEach
    void setud() {
        MockitoAnnotations.openMocks(this);

        authRequest = AuthRequest.builder()
                .email(usuarioEmail)
                .password(password)
                .build();

        registerRequest = RegisterRequest.builder()
                .nombre(usuarioNombre)
                .email(usuarioEmail)
                .password(password)
                .build();
    }

    @Test
    @DisplayName("Deberia autenticar un usuario correctamente")
    void testLoginSuccess() {
        // Preparacion
        when(authService.login(usuarioEmail, password))
                .thenReturn(token);

        // Ejecucion
        ResponseEntity<?> response = authController.login(authRequest);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals(token, authResponse.getToken());
        assertEquals("Usuario Autenticado", authResponse.getMessage());

        verify(authService, times(1)).login(usuarioEmail, password);
    }

    @Test
    @DisplayName("Deberia retornar UNAUTHORIZED si falla iniciar sesion")
    void testLoginFail() {
        // Preparacion
        when(authService.login(usuarioEmail, password))
                .thenThrow(new AuthenticationFailedException("Credenciales invalidas."));

        // Ejecucion
        ResponseEntity<?> response = authController.login(authRequest);

        // Verificacion
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse authResponse = (ApiResponse) response.getBody();
        assertEquals("Credenciales invalidas.", authResponse.getMessage());
        assertEquals(false, authResponse.isSuccess());

        verify(authService, times(1)).login(usuarioEmail, password);
    }

    @Test
    @DisplayName("Deberia registrar un usuario correctamente")
    void testRegisterSuccess() {
        // Preparacion
        when(authService.register(usuarioNombre, usuarioEmail, password))
                .thenReturn(token);

        // Ejecucion
        ResponseEntity<?> response = authController.register(registerRequest);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals(token, authResponse.getToken());
        assertEquals("Usuario registrado.", authResponse.getMessage());

        verify(authService, times(1)).register(usuarioNombre, usuarioEmail, password);
    }

    @Test
    @DisplayName("Deberia retornar una BAD_REQUEST si el email esta en uso")
    void testRegisterEmailEnUso() {
        // Preparacion
        when(authService.register(usuarioNombre, usuarioEmail, password))
                .thenThrow(new EmailAlreadyExistsException("Email duplicado"));

        // Ejecucion
        ResponseEntity<?> response = authController.register(registerRequest);

        // Verificacion
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse authResponse = (ApiResponse) response.getBody();
        assertEquals("El correo electronico ya esta en uso.", authResponse.getMessage());
        assertEquals(false, authResponse.isSuccess());

        verify(authService, times(1)).register(usuarioNombre, usuarioEmail, password);
    }

    @Test
    @DisplayName("Deberia retornar un INTERNAL_SERVER_ERROR")
    void testRegisterUnexpectedError() {
        // Preparacion
        when(authService.register(usuarioNombre, usuarioEmail, password))
                .thenThrow(new RuntimeException("Error inesperado"));

        // Ejecucion
        ResponseEntity<?> response = authController.register(registerRequest);

        // Verificacion
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse authResponse = (ApiResponse) response.getBody();
        assertEquals("Error al registrar usuario.", authResponse.getMessage());
        assertEquals(false, authResponse.isSuccess());

        verify(authService, times(1)).register(usuarioNombre, usuarioEmail, password);
    }
}
