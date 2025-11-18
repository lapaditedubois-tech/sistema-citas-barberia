package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.BarberoServicioDTO;
import com.Neita.sistemacitasbarberia.service.BarberoServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbero-servicios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Barbero-Servicios", description = "Gestión de servicios ofrecidos por barberos")
public class BarberoServicioController {

    private final BarberoServicioService barberoServicioService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener relación barbero-servicio por ID")
    public ResponseEntity<BarberoServicioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(barberoServicioService.obtenerPorId(id));
    }

    @GetMapping("/profesional/{profesionalId}")
    @Operation(summary = "Obtener servicios de un profesional")
    public ResponseEntity<List<BarberoServicioDTO>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(barberoServicioService.obtenerPorProfesional(profesionalId));
    }

    @GetMapping("/servicio/{servicioId}")
    @Operation(summary = "Obtener profesionales que ofrecen un servicio")
    public ResponseEntity<List<BarberoServicioDTO>> obtenerPorServicio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(barberoServicioService.obtenerPorServicio(servicioId));
    }

    @GetMapping("/servicio/{servicioId}/disponibles")
    @Operation(summary = "Obtener profesionales disponibles para un servicio")
    public ResponseEntity<List<BarberoServicioDTO>> obtenerDisponiblesPorServicio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(barberoServicioService.obtenerProfesionalesDisponiblesPorServicio(servicioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Crear relación barbero-servicio")
    public ResponseEntity<BarberoServicioDTO> crear(
            @Valid @RequestBody BarberoServicioDTO.CrearBarberoServicioDTO crearDTO) {
        BarberoServicioDTO creado = barberoServicioService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}/precio")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Actualizar precio del servicio")
    public ResponseEntity<BarberoServicioDTO> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody BarberoServicioDTO.ActualizarPrecioDTO actualizarDTO) {
        return ResponseEntity.ok(barberoServicioService.actualizarPrecio(id, actualizarDTO));
    }

    @PutMapping("/{id}/disponibilidad")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Cambiar disponibilidad del servicio")
    public ResponseEntity<Void> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        barberoServicioService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Eliminar relación barbero-servicio")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        barberoServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
