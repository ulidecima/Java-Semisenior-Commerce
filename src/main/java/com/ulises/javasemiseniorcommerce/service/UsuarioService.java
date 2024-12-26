package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.exception.UserNotFoundException;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ulide
 */
@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    /**
     * Obtiene los datos de un usuario mediante su email
     * @param email Email del usuario
     * @return usuarioDto Datos del usuario
     */
    public UsuarioDto getUsuario(String email) {
        logger.info("Buscando usuario...");
        return mapToDto(getByEmail(email));
    }

    @Transactional
    public void updateUsuario(String email, UsuarioDto usuarioDto) {
        logger.info("Actualizando usuario...");

        UsuarioModel usuarioModel = getByEmail(email);

        try {
            usuarioModel.setNombre(usuarioDto.getNombre());
            usuarioModel.setEmail(usuarioDto.getEmail());
            usuarioModel.setHabilitado(usuarioDto.isHabilitado());

            usuarioRepository.save(usuarioModel);
            logger.info("Usuario actualizado correctamente");
        } catch (Exception e) {
            logger.error("Error al actualizar usuario. Error: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    public void deleteUsuario(String email) {
        logger.info("Eliminando usuario...");
        UsuarioModel usuario = getByEmail(email);

        try {
            usuarioRepository.delete(usuario);
            logger.info("Usuario eliminado correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar usuario. Error: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    private UsuarioModel getByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado.");
                    return new UserNotFoundException("Usuario no encontrado");
                });
    }

    private UsuarioDto mapToDto(UsuarioModel usuarioModel) {
        return UsuarioDto.builder()
                .id(usuarioModel.getId())
                .nombre(usuarioModel.getNombre())
                .email(usuarioModel.getEmail())
                .password(usuarioModel.getPassword())
                .habilitado(usuarioModel.isHabilitado())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Cargando un usuario para autenticacion...");
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
                });

    }
}
