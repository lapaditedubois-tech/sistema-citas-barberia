package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.ValoracionDTO;
import com.Neita.sistemacitasbarberia.entity.Cita;
import com.Neita.sistemacitasbarberia.entity.Profesional;
import com.Neita.sistemacitasbarberia.entity.Usuario;
import com.Neita.sistemacitasbarberia.entity.Valoracion;
import com.Neita.sistemacitasbarberia.exception.BadRequestException;
import com.Neita.sistemacitasbarberia.exception.ConflictException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.CitaRepository;
import com.Neita.sistemacitasbarberia.repository.ProfesionalRepository;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import com.Neita.sistemacitasbarberia.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ValoracionService {

    private static final Logger logger = LoggerFactory.getLogger(ValoracionService.class);
    
    private final ValoracionRepository valoracionRepository;
    private final ProfesionalRepository profesionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final ProfesionalService profesionalService;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ValoracionDTO obtenerPorId(Long id) {
        logger.debug("Buscando valoración con ID: {}", id);
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion", "id", id));
        return convertirADTO(valoracion);
    }

    @Transactional(readOnly = true)
    public List<ValoracionDTO> obtenerPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo valoraciones del profesional con ID: {}", profesionalId);
        return valoracionRepository.findByProfesionalIdOrderByFechaDesc(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ValoracionDTO> obtenerPorUsuario(Long usuarioId) {
        logger.debug("Obteniendo valoraciones del usuario con ID: {}", usuarioId);
        return valoracionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ValoracionDTO crear(ValoracionDTO.CrearValoracionDTO crearDTO) {
        logger.info("Creando nueva valoración");
        
        Profesional profesional = profesionalRepository.findById(crearDTO.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", crearDTO.getProfesionalId()));
        
        Usuario usuario = usuarioRepository.findById(crearDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", crearDTO.getUsuarioId()));
        
        Cita cita = null;
        if (crearDTO.getCitaId() != null) {
            cita = citaRepository.findById(crearDTO.getCitaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", crearDTO.getCitaId()));
            
            if (valoracionRepository.existsByCitaId(crearDTO.getCitaId())) {
                throw new ConflictException("Ya existe una valoración para esta cita");
            }
            
            if (!cita.getEstado().equals(Cita.EstadoCita.COMPLETADA)) {
                throw new BadRequestException("Solo se pueden valorar citas completadas");
            }
        }

        Valoracion valoracion = Valoracion.builder()
                .profesional(profesional)
                .usuario(usuario)
                .cita(cita)
                .calificacion(crearDTO.getCalificacion())
                .comentario(crearDTO.getComentario())
                .build();
        
        Valoracion valoracionGuardada = valoracionRepository.save(valoracion);
        
        // Actualizar calificación promedio del profesional
        profesionalService.actualizarCalificacion(profesional.getId());
        
        logger.info("Valoración creada exitosamente con ID: {}", valoracionGuardada.getId());
        
        return convertirADTO(valoracionGuardada);
    }

    public ValoracionDTO actualizar(Long id, ValoracionDTO valoracionDTO) {
        logger.info("Actualizando valoración con ID: {}", id);
        
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion", "id", id));
        
        valoracion.setCalificacion(valoracionDTO.getCalificacion());
        valoracion.setComentario(valoracionDTO.getComentario());
        
        Valoracion valoracionActualizada = valoracionRepository.save(valoracion);
        
        // Actualizar calificación promedio del profesional
        profesionalService.actualizarCalificacion(valoracion.getProfesional().getId());
        
        logger.info("Valoración actualizada exitosamente");
        
        return convertirADTO(valoracionActualizada);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando valoración con ID: {}", id);
        
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion", "id", id));
        
        Long profesionalId = valoracion.getProfesional().getId();
        
        valoracionRepository.deleteById(id);
        
        // Actualizar calificación promedio del profesional
        profesionalService.actualizarCalificacion(profesionalId);
        
        logger.info("Valoración eliminada exitosamente");
    }

    private ValoracionDTO convertirADTO(Valoracion valoracion) {
        ValoracionDTO dto = modelMapper.map(valoracion, ValoracionDTO.class);
        dto.setProfesionalId(valoracion.getProfesional().getId());
        dto.setUsuarioId(valoracion.getUsuario().getId());
        dto.setNombreUsuario(valoracion.getUsuario().getNombre());
        dto.setNombreProfesional(valoracion.getProfesional().getUsuario().getNombre());
        if (valoracion.getCita() != null) {
            dto.setCitaId(valoracion.getCita().getId());
        }
        return dto;
    }
}
