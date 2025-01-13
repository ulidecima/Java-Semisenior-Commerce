package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.dto.UsuarioRequest;
import com.ulises.javasemiseniorcommerce.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar usuarios
 *
 * @author ulide
 */
@Tag(name = "Usuarios", description = "Endpoint para gestionar usuarios")
@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtiene informacion de un usuario mediante su email.
     *
     * @param email es el Email del usuario.
     * @return Informacion del usuario.
     */
    @Operation(
            summary = "Obtener usuario",
            description = "Retorna la informacion de un usuario registrado con el email proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
            }
    )
    @GetMapping("/info/{email}")
    public ResponseEntity<UsuarioDto> getUsuario(@PathVariable("email") String email) {
        UsuarioDto usuario = usuarioService.getUsuario(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Actualiza la informacion de un usuario existente.
     *
     * @param email          es el Email del usuario.
     * @param usuarioRequest Son los datos actualizados del usuario.
     * @return Usuario con los datos actualizados.
     */
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la informacion de un usuario con el email proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos."),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")})
    @PutMapping("/{email}")
    public ResponseEntity<UsuarioDto> updateUsuario(
            @PathVariable("email") String email,
            @Valid @RequestBody UsuarioRequest usuarioRequest) {
        UsuarioDto usuarioDto = usuarioService.updateUsuario(email, usuarioRequest);
        return ResponseEntity.ok(usuarioDto);
    }

    /**
     * Elimina un usuario existente.
     *
     * @param email Es el email del usuario que se va a eliminar.
     * @return Mensaje sin contenido.
     */
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario registrado con el email proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente.")})
    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUsuario(@PathVariable String email) {
        usuarioService.deleteUsuario(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
