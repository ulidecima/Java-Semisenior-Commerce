package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.AuthRequest;
import com.ulises.javasemiseniorcommerce.dto.RegisterRequest;
import com.ulises.javasemiseniorcommerce.exception.badrquest.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.exception.unhauthorizated.AuthenticationFailedException;
import com.ulises.javasemiseniorcommerce.jwt.JwtService;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestDataFactory;
import com.ulises.javasemiseniorcommerce.testUtils.TestLoggerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
@ExtendWith(MockitoExtension.class)
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

    private static final String TOKEN = "mocked-jwt-token";

    @Nested
    @DisplayName("Tests de login.")
    class loginTests {
        @Test
        @DisplayName("Deberia autenticar correctamente y devolver un token JWT")
        void testLoginSuccess() {
            // Preparacion
            AuthRequest authRequest = TestDataFactory.crearAuthRequest();
            Authentication auth = mock(Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(jwtService.generateToken(any(), eq(authRequest.getEmail()))).thenReturn(TOKEN);

            // Ejecucion
            String result = authService.login(authRequest);

            // Verificacion
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService).generateToken(any(), eq(authRequest.getEmail()));
            assertEquals(TOKEN, result, "El token JWT generado no coincide con el esperado.");
        }

        @Test
        @DisplayName("Deberia lanzar una excepcion si la autenticacion falla")
        void testLoginfFail() {
            // Preparacion
            AuthRequest authRequest = TestDataFactory.crearAuthRequest();
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Autenticacion fallida."));

            // Ejecucion y Verificacion
            AuthenticationFailedException exception = assertThrows(
                    AuthenticationFailedException.class, () -> authService.login(authRequest));
            assertEquals("Credenciales invalidas.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests para registro.")
    class testRegister {
        @Test
        @DisplayName("Deberia registrar un usuario correctamente y generar un token JWT")
        void testRegisterSuccess() {
            // Preparacion
            RegisterRequest registerRequest = TestDataFactory.crearRegisterRequest();
            when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(jwtService.generateToken(any(), eq(registerRequest.getEmail()))).thenReturn(TOKEN);

            // Ejecucion
            String result = authService.register(registerRequest);

            // Verificacion
            verify(usuarioRepository).save(any(UsuarioModel.class));
            assertEquals(TOKEN, result);
        }

        @Test
        @DisplayName("Deberia lanzar una excepcion si ya existe un usuario registrado con el email proporcionado")
        void testRegisterEmailActualmenteEnUso() {
            // Preparacion
            RegisterRequest registerRequest = TestDataFactory.crearRegisterRequest();
            when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            // Ejecucion y verificacion
            EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                    () -> authService.register(registerRequest));
            assertEquals("Ya existe un usuario con este email.", exception.getMessage());
        }
    }
}
