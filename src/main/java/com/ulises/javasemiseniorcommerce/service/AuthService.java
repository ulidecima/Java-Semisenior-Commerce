package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.AuthRequest;
import com.ulises.javasemiseniorcommerce.dto.RegisterRequest;
import com.ulises.javasemiseniorcommerce.exception.unhauthorizated.AuthenticationFailedException;
import com.ulises.javasemiseniorcommerce.exception.badrquest.EmailAlreadyExistsException;
import com.ulises.javasemiseniorcommerce.jwt.JwtService;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
     * @param request Datos (email y contrasenia) para logear a un usuario registrado.
     * @return Token JWT
     */
    public String login(AuthRequest request) {
        logger.info("Auntenticando usuario con email: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            return generateToken(request.getEmail());
        } catch (BadCredentialsException e) {
            logger.warn("Credenciales invalidas para el usuario con email {}.", request.getEmail());
            // Excepcion por si el email o la contrasenia son incorrectos
            throw new AuthenticationFailedException("Credenciales invalidas.");
        }
    }

    /**
     * Metodo que crea un usuario nuevo y genera un Token JWT
     *
     * @param request Datos (nombre, email y contrasenia) para registrar al usuario nuevo.
     * @return Token JWT
     */
    public String register(RegisterRequest request) {
        logger.info("Registrando usuario con email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            logger.warn("El email ya esta en uso: {}", request.getEmail());
            // Excepcion por si el correo electronico ya esta registrado
            throw new EmailAlreadyExistsException("El correo electronico ya esta registrado.");
        }

        UsuarioModel usuario = UsuarioModel.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        usuarioRepository.save(usuario);
        logger.info("Usuario registrado correctamente: {}", request.getEmail());
        return generateToken(request.getEmail());
    }

    // Se genera el token JWT
    private String generateToken(String email) {
        Map<String, Object> claims = Map.of("email", email);
        return jwtService.generateToken(claims, email);
    }
}
