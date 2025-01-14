package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.UsuarioDto;
import com.ulises.javasemiseniorcommerce.dto.UsuarioRequest;
import com.ulises.javasemiseniorcommerce.exception.EmailAlreadyExistsException;
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
     *
     * @param email Email del usuario
     * @return usuarioDto Datos del usuario
     */
    public UsuarioDto getUsuario(String email) {
        logger.info("Buscando usuario...");
        return mapToDto(getByEmail(email));
    }

    /**
     * Actualiza los datos de un usuario.
     *
     * @param email Email del usuario.
     * @param usuarioRequest Datos actualizados del usuario.
     * @return usuarioDto Los datos actualizados del usuario.
     */
    @Transactional
    public UsuarioDto updateUsuario(String email, UsuarioRequest usuarioRequest) {
        logger.info("Actualizando usuario...");

        UsuarioModel usuarioModel = getByEmail(email);

        // Excepcion por si el email ya esya registrado
        if (!email.equals(usuarioRequest.getEmail()) && usuarioRepository.existsByEmail(usuarioRequest.getEmail()))
            throw new EmailAlreadyExistsException("El email ya esta en uso.");

        usuarioModel.setNombre(usuarioRequest.getNombre());
        usuarioModel.setEmail(usuarioRequest.getEmail());
        usuarioModel.setHabilitado(usuarioRequest.isHabilitado());

        usuarioRepository.save(usuarioModel);
        logger.info("Usuario actualizado correctamente.");
        return mapToDto(usuarioModel);
    }

    /**
     * Elimina un usuario mediante su email.
     *
     * @param email Email del usuario que se va a eliminar.
     */
    @Transactional
    public void deleteUsuario(String email) {
        logger.info("Eliminando usuario...");

        usuarioRepository.findByEmail(email).ifPresent(usuarioRepository::delete);
        logger.info("Usuario eliminado correctamente.");
    }

    /**
     * Obtiene un usuario emdiante su email.
     *
     * @param email Email del usuario.
     * @return UsuarioModel con los datos del usuario.
     */
    private UsuarioModel getByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Usuario no encontrado.")
        );
    }

    // Metodo para mapear un UsuarioModel hacia un UsuarioDto
    private UsuarioDto mapToDto(UsuarioModel usuarioModel) {
        return UsuarioDto.builder()
                .id(usuarioModel.getId())
                .nombre(usuarioModel.getNombre())
                .email(usuarioModel.getEmail())
                //.password(usuarioModel.getPassword())
                .password("********") // por seguridad el password no se muestra
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
