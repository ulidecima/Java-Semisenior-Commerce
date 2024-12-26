package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.exception.UserNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioModel usuarioModel;
    private UsuarioDto usuarioDto;

    private static String nombre = "Test usuario";
    private static String email = "test@mail.com";
    private static String password = "password";
    private static boolean habilitado = true;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuarioModel = UsuarioModel.builder()
                .id(1L)
                .nombre(nombre)
                .email(email)
                .password(password)
                .habilitado(habilitado)
                .build();

        usuarioDto = UsuarioDto.builder()
                .id(1L)
                .nombre(nombre)
                .email(email)
                .password(password)
                .habilitado(habilitado)
                .build();
    }

    @Test
    @DisplayName("Deberia devolver la informacion completa de un usuario")
    void testGetUsuarioSuccess() {
        // Preparacion
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuarioModel));

        // Ejecucion
        UsuarioDto result = usuarioService.getUsuario(email);

        // Verificacion
        assertNotNull(result);
        assertEquals(nombre, result.getNombre());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deberia lanzar una UsuarioNotFoundException")
    void testGetUsuarioNotFound() {
        // Preparacion
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Ejecucion
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            usuarioService.getUsuario(email);
        });

        // Verificacion
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deberia actualizar un usuario correctamente")
    void testUpdateUsuarioSuccess() {
        // Preparacion
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuarioModel));

        // Ejecucion
        usuarioService.updateUsuario(email, usuarioDto);

        // Verificacion
        assertEquals(usuarioDto.getNombre(), usuarioModel.getNombre());
        assertEquals(usuarioDto.getEmail(), usuarioModel.getEmail());
        assertEquals(usuarioDto.getPassword(), usuarioModel.getPassword());
        assertEquals(usuarioDto.isHabilitado(), usuarioModel.isHabilitado());
        verify(usuarioRepository, times(1)).save(usuarioModel);
    }

    @Test
    @DisplayName("Deberia lanzar una UserNotFoundException")
    void testUpdateUsuarioNotFound() {
        // Preparacion
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Ejecucion
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            usuarioService.updateUsuario(email, usuarioDto);
        });

        // Verificacion
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deberia eliminar un usuario correctamente")
    void testDeleteUsuarioSuccess() {
        // Preparacion
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioModel));

        // Ejecucion
        usuarioService.deleteUsuario(email);

        // Verificacion
        verify(usuarioRepository, times(1)).delete(usuarioModel);
    }

    @Test
    @DisplayName("Deberia lanzar una UserNotFOundException")
    void testDeleteUsuarioNotFound() {
        // Preparacion
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Ejecucion
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            usuarioService.deleteUsuario(email);
        });

        // Verificacion
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).delete(any());
    }
}

