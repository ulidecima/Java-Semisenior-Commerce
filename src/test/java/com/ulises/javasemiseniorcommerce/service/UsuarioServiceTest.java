package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.dto.UsuarioRequest;
import com.ulises.javasemiseniorcommerce.exception.UserNotFoundException;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import com.ulises.javasemiseniorcommerce.testUtils.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Nested
    @DisplayName("UsuarioService Get Tests")
    class UsuarioServiceGetTests {

        @Test
        @DisplayName("Deberia devolver la informacion completa de un usuario")
        void testGetUsuarioSuccess() {
            // Preparacion
            UsuarioModel usuario = UsuarioModel.builder()
                    .nombre("Usuario Test")
                    .email("test@mail.com")
                    .build();

            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));

            // Ejecucion
            UsuarioDto result = usuarioService.getUsuario(usuario.getEmail());

            // Verificacion
            assertNotNull(result);
            assertEquals(usuario.getNombre(), result.getNombre());
            assertEquals(usuario.getEmail(), result.getEmail());
            verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        }

        @Test
        @DisplayName("Deberia lanzar una UsuarioNotFoundException")
        void testGetUsuarioNotFound() {
            // Preparacion
            UsuarioModel usuario = UsuarioModel.builder()
                    .email("test@mail.com")
                    .build();
            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.empty());

            // Ejecucion
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () -> usuarioService.getUsuario(usuario.getEmail()));

            // Verificacion
            assertEquals("Usuario no encontrado.", exception.getMessage());
            verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        }
    }

    @Nested
    @DisplayName("UsuarioService Update Tests")
    class UsuarioServiceUpdateTests {
        @Test
        @DisplayName("Deberia actualizar un usuario correctamente")
        void testUpdateUsuarioSuccess() {
            // Preparacion
            UsuarioModel usuario = TestDataFactory.crearUsuarioModel();
            UsuarioRequest usuarioRequest = TestDataFactory.crearUsuarioRequest();
            when(usuarioRepository.findByEmail(usuario.getEmail()))
                    .thenReturn(Optional.of(usuario));

            // Ejecucion
            usuarioService.updateUsuario(usuario.getEmail(), usuarioRequest);

            // Verificacion
            ArgumentCaptor<UsuarioModel> captor = ArgumentCaptor.forClass(UsuarioModel.class);
            verify(usuarioRepository).save(captor.capture());
            UsuarioModel updatedUser = captor.getValue();

            assertEquals(usuarioRequest.getNombre(), updatedUser.getNombre());
            assertEquals(usuarioRequest.getEmail(), updatedUser.getEmail());
        }

        @Test
        @DisplayName("Deberia lanzar una UserNotFoundException")
        void testUpdateUsuarioNotFound() {
            // Preparacion
            UsuarioModel usuario = UsuarioModel.builder()
                    .email("test@mail.com")
                    .build();
            UsuarioRequest usuarioRequest = UsuarioRequest.builder()
                    .email(usuario.getEmail())
                    .build();
            when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());

            // Ejecucion
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () -> usuarioService.updateUsuario(usuario.getEmail(), usuarioRequest));

            // Verificacion
            assertEquals("Usuario no encontrado.", exception.getMessage());
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("UsuarioService Delete Tests")
    class UsuarioServiceDeleteTests {
        @Test
        @DisplayName("Deberia eliminar un usuario correctamente")
        void testDeleteUsuarioSuccess() {
            // Preparacion
            UsuarioModel usuario = UsuarioModel.builder()
                    .email("test@mail.com")
                    .build();
            when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

            // Ejecucion
            usuarioService.deleteUsuario(usuario.getEmail());

            // Verificacion
            verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
            verify(usuarioRepository, times(1)).delete(usuario);
        }
    }
}

