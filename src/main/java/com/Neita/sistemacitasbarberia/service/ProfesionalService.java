package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.ProfesionalDTO;
import com.Neita.sistemacitasbarberia.entity.Profesional;
import com.Neita.sistemacitasbarberia.entity.Usuario;
import com.Neita.sistemacitasbarberia.exception.BadRequestException;
import com.Neita.sistemacitasbarberia.exception.ConflictException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.ProfesionalRepository;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfesionalService {

    private static final Logger logger = LoggerFactory.getLogger(ProfesionalService.class);
    
    private final ProfesionalRepository profesionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "profesionales", key = "#id")
    public ProfesionalDTO obtenerPorId(Long id) {
        logger.debug("Buscando profesional con ID: {}", id);
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", id));
        return convertirADTO(profesional);
    }

    @Transactional(readOnly = true)
    public ProfesionalDTO obtenerPorUsuarioId(Long usuarioId) {
        logger.debug("Buscando profesional del usuario con ID: {}", usuarioId);
        Profesional profesional = profesionalRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "usuarioId", usuarioId));
        return convertirADTO(profesional);
    }

    @Transactional(readOnly = true)
    public List<ProfesionalDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los profesionales");
        return profesionalRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfesionalDTO> obtenerActivos() {
        logger.debug("Obteniendo profesionales activos");
        return profesionalRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfesionalDTO> obtenerPorEspecialidad(String especialidad) {
        logger.debug("Buscando profesionales con especialidad: {}", especialidad);
        return profesionalRepository.findByEspecialidadContainingIgnoreCaseAndActivoTrue(especialidad).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfesionalDTO> obtenerMejorCalificados() {
        logger.debug("Obteniendo profesionales mejor calificados");
        return profesionalRepository.findTopRated().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfesionalDTO> obtenerPorServicio(Long servicioId) {
        logger.debug("Obteniendo profesionales que ofrecen el servicio con ID: {}", servicioId);
        return profesionalRepository.findByServicioId(servicioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "profesionales", allEntries = true)
    public ProfesionalDTO crear(ProfesionalDTO.CrearProfesionalDTO crearDTO) {
        logger.info("Creando nuevo profesional para usuario ID: {}", crearDTO.getUsuarioId());
        
        Usuario usuario = usuarioRepository.findById(crearDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", crearDTO.getUsuarioId()));
        
        if (profesionalRepository.existsByUsuarioId(crearDTO.getUsuarioId())) {
            throw new ConflictException("Ya existe un perfil profesional para este usuario");
        }

        Profesional profesional = Profesional.builder()
                .usuario(usuario)
                .especialidad(crearDTO.getEspecialidad())
                .horarioDisponible(crearDTO.getHorarioDisponible())
                .biografia(crearDTO.getBiografia())
                .activo(true)
                .calificacionPromedio(0.0)
                .build();
        
        usuario.agregarRol("ROLE_PROFESIONAL");
        usuarioRepository.save(usuario);
        
        Profesional profesionalGuardado = profesionalRepository.save(profesional);
        logger.info("Profesional creado exitosamente con ID: {}", profesionalGuardado.getId());
        
        return convertirADTO(profesionalGuardado);
    }

    @CacheEvict(value = "profesionales", key = "#id")
    public ProfesionalDTO actualizar(Long id, ProfesionalDTO profesionalDTO) {
        logger.info("Actualizando profesional con ID: {}", id);
        
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", id));
        
        profesional.setEspecialidad(profesionalDTO.getEspecialidad());
        profesional.setHorarioDisponible(profesionalDTO.getHorarioDisponible());
        profesional.setBiografia(profesionalDTO.getBiografia());
        
        if (profesionalDTO.getFotoPerfil() != null) {
            profesional.setFotoPerfil(profesionalDTO.getFotoPerfil());
        }
        
        Profesional profesionalActualizado = profesionalRepository.save(profesional);
        logger.info("Profesional actualizado exitosamente");
        
        return convertirADTO(profesionalActualizado);
    }

    @CacheEvict(value = "profesionales", key = "#id")
    public void actualizarCalificacion(Long id) {
        logger.info("Actualizando calificación del profesional con ID: {}", id);
        
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", id));
        
        profesional.actualizarCalificacionPromedio();
        profesionalRepository.save(profesional);
        
        logger.info("Calificación actualizada: {}", profesional.getCalificacionPromedio());
    }

    @CacheEvict(value = "profesionales", key = "#id")
    public void desactivar(Long id) {
        logger.info("Desactivando profesional con ID: {}", id);
        
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", id));
        
        profesional.setActivo(false);
        profesionalRepository.save(profesional);
        
        logger.info("Profesional desactivado exitosamente");
    }

    @CacheEvict(value = "profesionales", key = "#id")
    public void activar(Long id) {
        logger.info("Activando profesional con ID: {}", id);
        
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", id));
        
        profesional.setActivo(true);
        profesionalRepository.save(profesional);
        
        logger.info("Profesional activado exitosamente");
    }

    @CacheEvict(value = "profesionales", key = "#id")
    public void eliminar(Long id) {
        logger.info("Eliminando profesional con ID: {}", id);
        
        if (!profesionalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesional", "id", id);
        }
        
        profesionalRepository.deleteById(id);
        logger.info("Profesional eliminado exitosamente");
    }

    private ProfesionalDTO convertirADTO(Profesional profesional) {
        ProfesionalDTO dto = modelMapper.map(profesional, ProfesionalDTO.class);
        dto.setUsuarioId(profesional.getUsuario().getId());
        dto.setNombreUsuario(profesional.getUsuario().getNombre());
        dto.setEmailUsuario(profesional.getUsuario().getEmail());
        dto.setTelefonoUsuario(profesional.getUsuario().getTelefono());
        return dto;
    }
}
