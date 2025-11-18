package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.ServicioDTO;
import com.Neita.sistemacitasbarberia.entity.Servicio;
import com.Neita.sistemacitasbarberia.exception.ConflictException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.ServicioRepository;
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
public class ServicioService {

    private static final Logger logger = LoggerFactory.getLogger(ServicioService.class);
    
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "servicios", key = "#id")
    public ServicioDTO obtenerPorId(Long id) {
        logger.debug("Buscando servicio con ID: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", id));
        return convertirADTO(servicio);
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los servicios");
        return servicioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO> obtenerActivos() {
        logger.debug("Obteniendo servicios activos");
        return servicioRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO> buscarPorNombre(String nombre) {
        logger.debug("Buscando servicios con nombre: {}", nombre);
        return servicioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO> obtenerPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo servicios del profesional con ID: {}", profesionalId);
        return servicioRepository.findByProfesionalId(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "servicios", allEntries = true)
    public ServicioDTO crear(ServicioDTO servicioDTO) {
        logger.info("Creando nuevo servicio: {}", servicioDTO.getNombre());

        Servicio servicio = Servicio.builder()
                .nombre(servicioDTO.getNombre())
                .descripcion(servicioDTO.getDescripcion())
                .duracion(servicioDTO.getDuracion())
                .imagenServicio(servicioDTO.getImagenServicio())
                .activo(true)
                .build();
        
        Servicio servicioGuardado = servicioRepository.save(servicio);
        logger.info("Servicio creado exitosamente con ID: {}", servicioGuardado.getId());
        
        return convertirADTO(servicioGuardado);
    }

    @CacheEvict(value = "servicios", key = "#id")
    public ServicioDTO actualizar(Long id, ServicioDTO servicioDTO) {
        logger.info("Actualizando servicio con ID: {}", id);
        
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", id));
        
        servicio.setNombre(servicioDTO.getNombre());
        servicio.setDescripcion(servicioDTO.getDescripcion());
        servicio.setDuracion(servicioDTO.getDuracion());
        
        if (servicioDTO.getImagenServicio() != null) {
            servicio.setImagenServicio(servicioDTO.getImagenServicio());
        }
        
        Servicio servicioActualizado = servicioRepository.save(servicio);
        logger.info("Servicio actualizado exitosamente");
        
        return convertirADTO(servicioActualizado);
    }

    @CacheEvict(value = "servicios", key = "#id")
    public void desactivar(Long id) {
        logger.info("Desactivando servicio con ID: {}", id);
        
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", id));
        
        servicio.setActivo(false);
        servicioRepository.save(servicio);
        
        logger.info("Servicio desactivado exitosamente");
    }

    @CacheEvict(value = "servicios", key = "#id")
    public void activar(Long id) {
        logger.info("Activando servicio con ID: {}", id);
        
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", id));
        
        servicio.setActivo(true);
        servicioRepository.save(servicio);
        
        logger.info("Servicio activado exitosamente");
    }

    @CacheEvict(value = "servicios", key = "#id")
    public void eliminar(Long id) {
        logger.info("Eliminando servicio con ID: {}", id);
        
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio", "id", id);
        }
        
        servicioRepository.deleteById(id);
        logger.info("Servicio eliminado exitosamente");
    }

    private ServicioDTO convertirADTO(Servicio servicio) {
        return modelMapper.map(servicio, ServicioDTO.class);
    }
}
