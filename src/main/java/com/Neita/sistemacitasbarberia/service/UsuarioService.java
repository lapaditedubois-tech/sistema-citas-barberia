package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.UsuarioDTO;
import com.Neita.sistemacitasbarberia.entity.Usuario;
import com.Neita.sistemacitasbarberia.exception.BadRequestException;
import com.Neita.sistemacitasbarberia.exception.ConflictException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioDTO obtenerPorId(Long id) {
        logger.debug("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return convertirADTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email) {
        logger.debug("Buscando usuario con email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return convertirADTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerActivos() {
        logger.debug("Obteniendo usuarios activos");
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerPorRol(String rol) {
        logger.debug("Obteniendo usuarios con rol: {}", rol);
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public UsuarioDTO registrar(UsuarioDTO.RegistroDTO registroDTO) {
        logger.info("Registrando nuevo usuario con email: {}", registroDTO.getEmail());
        
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new ConflictException("Ya existe un usuario con el email: " + registroDTO.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombre(registroDTO.getNombre())
                .email(registroDTO.getEmail())
                .password(passwordEncoder.encode(registroDTO.getPassword()))
                .telefono(registroDTO.getTelefono())
                .activo(true)
                .build();
        
        usuario.agregarRol("ROLE_CLIENTE");
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return convertirADTO(usuarioGuardado);
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public UsuarioDTO actualizar(Long id, UsuarioDTO.ActualizarPerfilDTO actualizarDTO) {
        logger.info("Actualizando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setNombre(actualizarDTO.getNombre());
        usuario.setTelefono(actualizarDTO.getTelefono());
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente");
        
        return convertirADTO(usuarioActualizado);
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public void cambiarPassword(Long id, UsuarioDTO.CambiarPasswordDTO cambiarPasswordDTO) {
        logger.info("Cambiando contraseña para usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        if (!passwordEncoder.matches(cambiarPasswordDTO.getPasswordActual(), usuario.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }
        
        usuario.setPassword(passwordEncoder.encode(cambiarPasswordDTO.getPasswordNueva()));
        usuarioRepository.save(usuario);
        
        logger.info("Contraseña cambiada exitosamente");
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public void desactivar(Long id) {
        logger.info("Desactivando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
        logger.info("Usuario desactivado exitosamente");
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public void activar(Long id) {
        logger.info("Activando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        
        logger.info("Usuario activado exitosamente");
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public void eliminar(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        
        usuarioRepository.deleteById(id);
        logger.info("Usuario eliminado exitosamente");
    }

    @CacheEvict(value = "usuarios", key = "#id")
    public void agregarRol(Long id, String rol) {
        logger.info("Agregando rol {} al usuario con ID: {}", rol, id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.agregarRol(rol);
        usuarioRepository.save(usuario);
        
        logger.info("Rol agregado exitosamente");
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
        dto.setRoles(usuario.getRoles());
        return dto;
    }
}
