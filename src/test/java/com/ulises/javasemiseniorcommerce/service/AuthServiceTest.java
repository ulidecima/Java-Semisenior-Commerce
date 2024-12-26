package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.exception.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.jwt.JwtService;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String email = "test@mail.com";
    private final String password = "password";
    private final String nombre = "mockedJwtToken";
    private final String token = "mocked-jwt-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deberia autenticar correctamente y devolver un token JWT")
    void testLoginSuccess() {
        // Preparacion
        Authentication auth = mock(Authentication.class);
        when(authenticationManager
                .authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.generateToken(any(), eq(email)))
                .thenReturn(token);

        // Ejecucion
        String result = authService.login(email, password);

        // Verificacion
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals(token, result, "El token JWT generado no coincide con el esperado.");
    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si la autenticacion falla")
    void testLoginfFail() {
        // Preparacion
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Autenticacion fallida."));

        // Ejecucion y Verificacion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, password));
        assertEquals("Error inespetado al autenticar usuario.", exception.getMessage());
    }

    @Test
    @DisplayName("Deberia registrar un usuario correctamente y generar un token JWT")
    void testRegisterSuccess() {
        // Preparacion
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(), eq(email))).thenReturn(token);

        // Ejecucion
        String result = authService.register(nombre, email, password);

        // Verificacion
        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(jwtService).generateToken(any(), eq(email));
        assertEquals(token, result);
    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si ya existe un usuario registrado con el email proporcionado")
    void testRegisterEmailActualmenteEnUso() {
        // Preparacion
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);

        // Ejecucion y verificacion
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(nombre, email, password);
        });
        assertEquals("Ya existe un usuario con este email.", exception.getMessage());
    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si ocurre algun error al guardar el usuario")
    void testRegisterFail() {
        // Preparacion: simulamos que el usuario ya existe para que falle la creacion
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded-Password");
        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenThrow(new RuntimeException("Error al guardar el usuario"));

        // Ejecucion y verificacion
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(nombre, email, password);
        });
        assertEquals("Error inespetado al registrar usuario.", exception.getMessage());
    }
}
