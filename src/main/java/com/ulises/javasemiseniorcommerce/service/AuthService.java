package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.exception.AuthenticationFailedException;
import com.ulises.javasemiseniorcommerce.exception.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.jwt.JwtService;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author ulide
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Metodo que logea a un usuario y luego genera un token JWT
     *
     * @param email    Correo electronico del usuario
     * @param password Contrasenia del usuario
     * @return Token JWT
     */
    public String login(String email, String password) {
        logger.info("Auntenticando usuario con email: {}", email);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return generateToken(email);
        } catch (AuthenticationFailedException e) {
            logger.warn("Error al autenticar el usuario con email: {}", email);
            throw new AuthenticationFailedException("Credenciales invalidas");
        } catch (Exception e) {
            logger.error("Error inesperado al autenticar usuario. Error {}", e.getMessage());
            throw new RuntimeException("Error inespetado al autenticar usuario.");
        }
    }

    /**
     * Metodo que crea un usuario nuevo y genera un Token JWT
     *
     * @param nombre   Nombre del usuario que se va a registrar
     * @param email    Correo electronico del usuario
     * @param password Contrasenia del usuario
     * @return Token JWT
     */
    public String register(String nombre, String email, String password) {
        logger.info("Registrando un usuario con email: {}", email);
        if (usuarioRepository.existsByEmail(email)) {
            logger.warn("Error al registrar usuario. El email ya esta en uso: {}", email);
            throw new EmailAlreadyExistsException("Ya existe un usuario con este email.");
        }

        try {
            UsuarioModel usuario = UsuarioModel.builder()
                    .nombre(nombre)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .build();

            usuarioRepository.save(usuario);
            logger.info("Registro correcto para el usuario con email: {}", email);
            return generateToken(email);
        } catch (Exception e) {
            logger.error("Error inesperado al registrar usuario. Error {}", e.getMessage());
            throw new RuntimeException("Error inespetado al registrar usuario.");
        }
    }

    private String generateToken(String email) {
        Map<String, Object> claims = Map.of("email", email);
        return jwtService.generateToken(claims, email);
    }
}
