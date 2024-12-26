package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ApiResponse;
import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.exception.UserNotFoundException;
import com.ulises.javasemiseniorcommerce.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para gestionar usuarios
 * @author ulide
 */
@Tag(name = "Usuarios", description = "Endpoint para gestionar usuarios")
@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    /**
     * Obtiene informacion de un usuario mediante su email
     * @param email es el Email del usuario
     * @return Informacion del usuario
     */
    @Operation(summary = "Obtener usuario", description = "Retorna la informacion de un usuario registrado con el email proporcionado.")
    @GetMapping("/info/{email}")
    public ResponseEntity<?> getUsuario(@PathVariable("email") String email) {
        logger.info("GET_USUARIO_REQUEST: email recibido: {}", email);
        try {
            UsuarioDto usuario = usuarioService.getUsuario(email);
            logger.info("GET_USUARIO_SUCCESS: Usuario encontrado con email {}", email);
            return ResponseEntity.ok(usuario);
        } catch (UserNotFoundException e) {
            logger.warn("GET_USUARIO_WARNING: Usuario no encontrado con email {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Usuario no encontrado", false));
        } catch (Exception e) {
            logger.error("GET_USUARIO_ERROR: Error obteniendo el usuario con email {}. Error {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al obtener el usuario.", false));
        }
    }

    /**
     * Actualiza la informacion de un usuario existente
     * @param email es el Email del usuario
     * @param usuarioDto Son los datos actualizados del usuario
     * @return Mensaje de exito
     */
    @Operation(summary = "Actualizar usuario", description = "Actualiza la informacion de un usuario con el email proporcionado.")
    @PutMapping("/{email}")
    public ResponseEntity<ApiResponse> updateUsuario(
            @PathVariable("email") String email,
            @Valid @RequestBody UsuarioDto usuarioDto) {
        logger.info("UPDATE_USUARIO_REQUEST: Email: {}", email);

        try {
            usuarioService.updateUsuario(email, usuarioDto);
            return ResponseEntity.ok(new ApiResponse("UPDATE_USUARIO_SUCCESS: Usuario actualizado correctamente", true));
        } catch (UserNotFoundException e) {
            logger.warn("UPDATE_USUARIO_WARNING: Usuario no encontrado con email: {}. Error {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Usuario no encontrado", false));
        } catch (Exception e) {
            logger.error("UPDATE_USUARIO_ERROR: Error actualizando usuario con email: {}. Error: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al actualizar el usuario", false));
        }
    }

    /**
     * Elimina un usuario existente
     * @param email Es el email del usuario que se va a eliminar
     * @return Mensaje de exito
     */
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario registrado con el email proporcionado.")
    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse> deleteUsuario(@PathVariable String email) {
        logger.info("DELETE_USUARIO_REQUEST: Email recibido: {}", email);

        try {
            usuarioService.deleteUsuario(email);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UserNotFoundException e) {
            logger.warn("DELETE_USUARIO_WARNING: Usuario no encontrado con email: {}. Error {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Usuario no encontrado", false));
        } catch (Exception e) {
            logger.error("DELETE_USUARIO_ERROR: Error eliminando usuario con email: {}. Error: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al eliminar el usuario", false));
        }
    }
}
