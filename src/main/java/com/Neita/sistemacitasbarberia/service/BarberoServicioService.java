package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.BarberoServicioDTO;
import com.Neita.sistemacitasbarberia.entity.BarberoServicio;
import com.Neita.sistemacitasbarberia.entity.Profesional;
import com.Neita.sistemacitasbarberia.entity.Servicio;
import com.Neita.sistemacitasbarberia.exception.ConflictException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.BarberoServicioRepository;
import com.Neita.sistemacitasbarberia.repository.ProfesionalRepository;
import com.Neita.sistemacitasbarberia.repository.ServicioRepository;
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
public class BarberoServicioService {

    private static final Logger logger = LoggerFactory.getLogger(BarberoServicioService.class);
    
    private final BarberoServicioRepository barberoServicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public BarberoServicioDTO obtenerPorId(Long id) {
        logger.debug("Buscando barbero-servicio con ID: {}", id);
        BarberoServicio bs = barberoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BarberoServicio", "id", id));
        return convertirADTO(bs);
    }

    @Transactional(readOnly = true)
    public List<BarberoServicioDTO> obtenerPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo servicios del profesional con ID: {}", profesionalId);
        return barberoServicioRepository.findByProfesionalId(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BarberoServicioDTO> obtenerPorServicio(Long servicioId) {
        logger.debug("Obteniendo profesionales que ofrecen el servicio con ID: {}", servicioId);
        return barberoServicioRepository.findByServicioId(servicioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BarberoServicioDTO> obtenerProfesionalesDisponiblesPorServicio(Long servicioId) {
        logger.debug("Obteniendo profesionales disponibles para servicio con ID: {}", servicioId);
        return barberoServicioRepository.findProfesionalesDisponiblesByServicio(servicioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public BarberoServicioDTO crear(BarberoServicioDTO.CrearBarberoServicioDTO crearDTO) {
        logger.info("Creando relación barbero-servicio");
        
        Profesional profesional = profesionalRepository.findById(crearDTO.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", crearDTO.getProfesionalId()));
        
        Servicio servicio = servicioRepository.findById(crearDTO.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", crearDTO.getServicioId()));
        
        if (barberoServicioRepository.existsByProfesionalIdAndServicioId(
                crearDTO.getProfesionalId(), crearDTO.getServicioId())) {
            throw new ConflictException("El profesional ya ofrece este servicio");
        }

        BarberoServicio barberoServicio = BarberoServicio.builder()
                .profesional(profesional)
                .servicio(servicio)
                .precio(crearDTO.getPrecio())
                .notasEspeciales(crearDTO.getNotasEspeciales())
                .disponible(true)
                .build();
        
        BarberoServicio guardado = barberoServicioRepository.save(barberoServicio);
        logger.info("Relación barbero-servicio creada exitosamente con ID: {}", guardado.getId());
        
        return convertirADTO(guardado);
    }

    public BarberoServicioDTO actualizarPrecio(Long id, BarberoServicioDTO.ActualizarPrecioDTO actualizarDTO) {
        logger.info("Actualizando precio de barbero-servicio con ID: {}", id);
        
        BarberoServicio barberoServicio = barberoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BarberoServicio", "id", id));
        
        barberoServicio.setPrecio(actualizarDTO.getPrecio());
        barberoServicio.setNotasEspeciales(actualizarDTO.getNotasEspeciales());
        
        BarberoServicio actualizado = barberoServicioRepository.save(barberoServicio);
        logger.info("Precio actualizado exitosamente");
        
        return convertirADTO(actualizado);
    }

    public void cambiarDisponibilidad(Long id, Boolean disponible) {
        logger.info("Cambiando disponibilidad de barbero-servicio con ID: {}", id);
        
        BarberoServicio barberoServicio = barberoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BarberoServicio", "id", id));
        
        barberoServicio.setDisponible(disponible);
        barberoServicioRepository.save(barberoServicio);
        
        logger.info("Disponibilidad actualizada a: {}", disponible);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando barbero-servicio con ID: {}", id);
        
        if (!barberoServicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("BarberoServicio", "id", id);
        }
        
        barberoServicioRepository.deleteById(id);
        logger.info("Relación barbero-servicio eliminada exitosamente");
    }

    private BarberoServicioDTO convertirADTO(BarberoServicio bs) {
        BarberoServicioDTO dto = modelMapper.map(bs, BarberoServicioDTO.class);
        dto.setProfesionalId(bs.getProfesional().getId());
        dto.setServicioId(bs.getServicio().getId());
        dto.setNombreServicio(bs.getServicio().getNombre());
        dto.setDescripcionServicio(bs.getServicio().getDescripcion());
        dto.setDuracionServicio(bs.getServicio().getDuracion());
        dto.setNombreProfesional(bs.getProfesional().getUsuario().getNombre());
        dto.setEspecialidadProfesional(bs.getProfesional().getEspecialidad());
        return dto;
    }
}
