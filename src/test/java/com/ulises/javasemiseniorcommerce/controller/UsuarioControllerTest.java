package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ApiResponse;
import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.service.UsuarioService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author ulide
 */
@ExtendWith(TestLoggerExtension.class)
public class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private UsuarioDto usuarioDto;

    private final static Long usuarioId = 1L;
    private final static String usuarioEmail = "test@mail.com";
    private final static String usuarioNombre = "Usuario Test";
    private final static String password = "psswrd";
    private final static boolean habilitado = true;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuarioDto = UsuarioDto.builder()
                .id(usuarioId)
                .nombre(usuarioNombre)
                .email(usuarioEmail)
                .password(password)
                .habilitado(habilitado)
                .build();
    }

    @Test
    @DisplayName("Deberia obtener un usuario mediante su email correctamente")
    void testGetUsuarioSuccess() {
        // Preparacion
        when(usuarioService.getUsuario(usuarioEmail))
                .thenReturn(usuarioDto);

        // Ejecucion
        ResponseEntity<?> response = usuarioController.getUsuario(usuarioEmail);

        // Verificacion
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioDto, response.getBody());
        verify(usuarioService, times(1)).getUsuario(usuarioEmail);
    }

    @Test
    @DisplayName("Deberia actualizar los datos de un usuario mediante su email correctamente")
    void testUpdateUsuarioSuccess() {
        // Preparacion
        doNothing().when(usuarioService).updateUsuario(usuarioEmail, usuarioDto);

        // Ejecucion
        ResponseEntity<?> response = usuarioController.updateUsuario(usuarioEmail, usuarioDto);

        // Verificacion;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals("UPDATE_USUARIO_SUCCESS: Usuario actualizado correctamente", apiResponse.getMessage());
        assertTrue(apiResponse.isSuccess());
        verify(usuarioService, times(1)).updateUsuario(usuarioEmail, usuarioDto);
    }

    @Test
    @DisplayName("Deberia eliminar un usuario correctamente")
    void testDeleteUsuarioSuccess() {
        // Preparacion
        doNothing().when(usuarioService).deleteUsuario(usuarioEmail);

        // Ejecucion
        ResponseEntity<?> response = usuarioController.deleteUsuario(usuarioEmail);

        // Verificacion
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(null, apiResponse);

        verify(usuarioService, times(1)).deleteUsuario(usuarioEmail);
    }
}
