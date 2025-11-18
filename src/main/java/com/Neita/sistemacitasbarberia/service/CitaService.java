package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.CitaDTO;
import com.Neita.sistemacitasbarberia.entity.*;
import com.Neita.sistemacitasbarberia.entity.Cita.EstadoCita;
import com.Neita.sistemacitasbarberia.exception.BadRequestException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaService {

    private static final Logger logger = LoggerFactory.getLogger(CitaService.class);
    
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final BarberoServicioRepository barberoServicioRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public CitaDTO obtenerPorId(Long id) {
        logger.debug("Buscando cita con ID: {}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        return convertirADTO(cita);
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerTodas() {
        logger.debug("Obteniendo todas las citas");
        return citaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerPorUsuario(Long usuarioId) {
        logger.debug("Obteniendo citas del usuario con ID: {}", usuarioId);
        return citaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo citas del profesional con ID: {}", profesionalId);
        return citaRepository.findByProfesionalId(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerProximasPorUsuario(Long usuarioId) {
        logger.debug("Obteniendo próximas citas del usuario con ID: {}", usuarioId);
        return citaRepository.findProximasCitasByUsuario(usuarioId, LocalDateTime.now()).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerProximasPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo próximas citas del profesional con ID: {}", profesionalId);
        return citaRepository.findProximasCitasByProfesional(profesionalId, LocalDateTime.now()).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerHistorialPorUsuario(Long usuarioId) {
        logger.debug("Obteniendo historial de citas del usuario con ID: {}", usuarioId);
        return citaRepository.findHistorialByUsuario(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CitaDTO crear(CitaDTO.CrearCitaDTO crearDTO) {
        logger.info("Creando nueva cita");
        
        Usuario usuario = usuarioRepository.findById(crearDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", crearDTO.getUsuarioId()));
        
        com.Neita.sistemacitasbarberia.entity.Servicio servicio = servicioRepository.findById(crearDTO.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", crearDTO.getServicioId()));
        
        Profesional profesional = profesionalRepository.findById(crearDTO.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", crearDTO.getProfesionalId()));
        
        // Validar que el profesional ofrece el servicio
        BarberoServicio barberoServicio = barberoServicioRepository
                .findByProfesionalIdAndServicioId(crearDTO.getProfesionalId(), crearDTO.getServicioId())
                .orElseThrow(() -> new BadRequestException("El profesional no ofrece este servicio"));
        
        if (!barberoServicio.getDisponible()) {
            throw new BadRequestException("El servicio no está disponible con este profesional");
        }
        
        // Validar disponibilidad de horario
        validarDisponibilidadHorario(crearDTO.getProfesionalId(), crearDTO.getFechaHora());

        Cita cita = Cita.builder()
                .usuario(usuario)
                .servicio(servicio)
                .profesional(profesional)
                .fechaHora(crearDTO.getFechaHora())
                .notas(crearDTO.getNotas())
                .estado(EstadoCita.PENDIENTE)
                .precioFinal(barberoServicio.getPrecio())
                .build();
        
        Cita citaGuardada = citaRepository.save(cita);
        logger.info("Cita creada exitosamente con ID: {}", citaGuardada.getId());
        
        return convertirADTO(citaGuardada);
    }

    public CitaDTO actualizarEstado(Long id, CitaDTO.ActualizarEstadoDTO actualizarDTO) {
        logger.info("Actualizando estado de cita con ID: {}", id);
        
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        
        cita.setEstado(actualizarDTO.getEstado());
        
        if (actualizarDTO.getNotas() != null) {
            cita.setNotas(actualizarDTO.getNotas());
        }
        
        if (actualizarDTO.getEstado() == EstadoCita.COMPLETADA) {
            cita.setFechaCompletada(LocalDateTime.now());
        }
        
        Cita citaActualizada = citaRepository.save(cita);
        logger.info("Estado de cita actualizado a: {}", actualizarDTO.getEstado());
        
        return convertirADTO(citaActualizada);
    }

    public CitaDTO actualizar(Long id, CitaDTO citaDTO) {
        logger.info("Actualizando cita con ID: {}", id);
        
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        
        if (!cita.puedeSerModificada()) {
            throw new BadRequestException("La cita no puede ser modificada en su estado actual");
        }
        
        if (citaDTO.getFechaHora() != null) {
            validarDisponibilidadHorario(cita.getProfesional().getId(), citaDTO.getFechaHora(), id);
            cita.setFechaHora(citaDTO.getFechaHora());
        }
        
        if (citaDTO.getNotas() != null) {
            cita.setNotas(citaDTO.getNotas());
        }
        
        Cita citaActualizada = citaRepository.save(cita);
        logger.info("Cita actualizada exitosamente");
        
        return convertirADTO(citaActualizada);
    }

    public void cancelar(Long id) {
        logger.info("Cancelando cita con ID: {}", id);
        
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        
        if (!cita.puedeSerModificada()) {
            throw new BadRequestException("La cita no puede ser cancelada en su estado actual");
        }
        
        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
        
        logger.info("Cita cancelada exitosamente");
    }

    public void eliminar(Long id) {
        logger.info("Eliminando cita con ID: {}", id);
        
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita", "id", id);
        }
        
        citaRepository.deleteById(id);
        logger.info("Cita eliminada exitosamente");
    }

    private void validarDisponibilidadHorario(Long profesionalId, LocalDateTime fechaHora) {
        validarDisponibilidadHorario(profesionalId, fechaHora, null);
    }

    private void validarDisponibilidadHorario(Long profesionalId, LocalDateTime fechaHora, Long citaIdExcluir) {
        LocalDateTime inicio = fechaHora.minusHours(1);
        LocalDateTime fin = fechaHora.plusHours(1);
        
        List<Cita> citasConflicto = citaRepository.findCitasActivasByProfesionalAndFecha(
                profesionalId, inicio, fin);
        
        if (citaIdExcluir != null) {
            citasConflicto = citasConflicto.stream()
                    .filter(c -> !c.getId().equals(citaIdExcluir))
                    .collect(Collectors.toList());
        }
        
        if (!citasConflicto.isEmpty()) {
            throw new BadRequestException("El profesional ya tiene una cita programada en ese horario");
        }
    }

    private CitaDTO convertirADTO(Cita cita) {
        CitaDTO dto = modelMapper.map(cita, CitaDTO.class);
        dto.setUsuarioId(cita.getUsuario().getId());
        dto.setServicioId(cita.getServicio().getId());
        dto.setProfesionalId(cita.getProfesional().getId());
        dto.setNombreUsuario(cita.getUsuario().getNombre());
        dto.setEmailUsuario(cita.getUsuario().getEmail());
        dto.setTelefonoUsuario(cita.getUsuario().getTelefono());
        dto.setNombreServicio(cita.getServicio().getNombre());
        dto.setDuracionServicio(cita.getServicio().getDuracion());
        dto.setNombreProfesional(cita.getProfesional().getUsuario().getNombre());
        dto.setEspecialidadProfesional(cita.getProfesional().getEspecialidad());
        return dto;
    }
}
