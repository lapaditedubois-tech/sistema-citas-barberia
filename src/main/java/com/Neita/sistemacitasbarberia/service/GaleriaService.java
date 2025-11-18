package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.GaleriaDTO;
import com.Neita.sistemacitasbarberia.entity.Galeria;
import com.Neita.sistemacitasbarberia.entity.Profesional;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.GaleriaRepository;
import com.Neita.sistemacitasbarberia.repository.ProfesionalRepository;
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
public class GaleriaService {

    private static final Logger logger = LoggerFactory.getLogger(GaleriaService.class);
    
    private final GaleriaRepository galeriaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public GaleriaDTO obtenerPorId(Long id) {
        logger.debug("Buscando entrada de galería con ID: {}", id);
        Galeria galeria = galeriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Galeria", "id", id));
        return convertirADTO(galeria);
    }

    @Transactional(readOnly = true)
    public List<GaleriaDTO> obtenerPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo galería del profesional con ID: {}", profesionalId);
        return galeriaRepository.findByProfesionalId(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GaleriaDTO> obtenerVisiblesPorProfesional(Long profesionalId) {
        logger.debug("Obteniendo galería visible del profesional con ID: {}", profesionalId);
        return galeriaRepository.findVisiblesByProfesional(profesionalId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GaleriaDTO> obtenerTodasVisibles() {
        logger.debug("Obteniendo todas las entradas visibles de galería");
        return galeriaRepository.findAllVisibles().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public GaleriaDTO crear(GaleriaDTO.CrearGaleriaDTO crearDTO) {
        logger.info("Creando nueva entrada de galería");
        
        Profesional profesional = profesionalRepository.findById(crearDTO.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", "id", crearDTO.getProfesionalId()));

        Galeria galeria = Galeria.builder()
                .profesional(profesional)
                .titulo(crearDTO.getTitulo())
                .descripcion(crearDTO.getDescripcion())
                .urlImagen(crearDTO.getUrlImagen())
                .visible(true)
                .build();
        
        Galeria galeriaGuardada = galeriaRepository.save(galeria);
        logger.info("Entrada de galería creada exitosamente con ID: {}", galeriaGuardada.getId());
        
        return convertirADTO(galeriaGuardada);
    }

    public GaleriaDTO actualizar(Long id, GaleriaDTO galeriaDTO) {
        logger.info("Actualizando entrada de galería con ID: {}", id);
        
        Galeria galeria = galeriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Galeria", "id", id));
        
        galeria.setTitulo(galeriaDTO.getTitulo());
        galeria.setDescripcion(galeriaDTO.getDescripcion());
        
        if (galeriaDTO.getUrlImagen() != null) {
            galeria.setUrlImagen(galeriaDTO.getUrlImagen());
        }
        
        Galeria galeriaActualizada = galeriaRepository.save(galeria);
        logger.info("Entrada de galería actualizada exitosamente");
        
        return convertirADTO(galeriaActualizada);
    }

    public void cambiarVisibilidad(Long id, Boolean visible) {
        logger.info("Cambiando visibilidad de galería con ID: {}", id);
        
        Galeria galeria = galeriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Galeria", "id", id));
        
        galeria.setVisible(visible);
        galeriaRepository.save(galeria);
        
        logger.info("Visibilidad actualizada a: {}", visible);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando entrada de galería con ID: {}", id);
        
        if (!galeriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Galeria", "id", id);
        }
        
        galeriaRepository.deleteById(id);
        logger.info("Entrada de galería eliminada exitosamente");
    }

    private GaleriaDTO convertirADTO(Galeria galeria) {
        GaleriaDTO dto = modelMapper.map(galeria, GaleriaDTO.class);
        dto.setProfesionalId(galeria.getProfesional().getId());
        dto.setNombreProfesional(galeria.getProfesional().getUsuario().getNombre());
        return dto;
    }
}
