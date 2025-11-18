package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.CitaDTO;
import com.Neita.sistemacitasbarberia.service.CitaService;
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
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Citas", description = "Gestión de citas de barbería")
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todas las citas")
    public ResponseEntity<List<CitaDTO>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @citaSecurity.esPropietarioCita(#id)")
    @Operation(summary = "Obtener cita por ID")
    public ResponseEntity<CitaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.esUsuarioActual(#usuarioId)")
    @Operation(summary = "Obtener citas de un usuario")
    public ResponseEntity<List<CitaDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/proximas")
    @Operation(summary = "Obtener próximas citas de un usuario")
    public ResponseEntity<List<CitaDTO>> obtenerProximasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.obtenerProximasPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @Operation(summary = "Obtener historial de citas de un usuario")
    public ResponseEntity<List<CitaDTO>> obtenerHistorialPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(citaService.obtenerHistorialPorUsuario(usuarioId));
    }

    @GetMapping("/profesional/{profesionalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Obtener citas de un profesional")
    public ResponseEntity<List<CitaDTO>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(citaService.obtenerPorProfesional(profesionalId));
    }

    @GetMapping("/profesional/{profesionalId}/proximas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Obtener próximas citas de un profesional")
    public ResponseEntity<List<CitaDTO>> obtenerProximasPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(citaService.obtenerProximasPorProfesional(profesionalId));
    }

    @PostMapping
    @Operation(summary = "Crear nueva cita")
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaDTO.CrearCitaDTO crearDTO) {
        CitaDTO citaCreada = citaService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(citaCreada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @citaSecurity.esPropietarioCita(#id)")
    @Operation(summary = "Actualizar cita")
    public ResponseEntity<CitaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaDTO citaDTO) {
        return ResponseEntity.ok(citaService.actualizar(id, citaDTO));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @Operation(summary = "Actualizar estado de cita")
    public ResponseEntity<CitaDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CitaDTO.ActualizarEstadoDTO actualizarDTO) {
        return ResponseEntity.ok(citaService.actualizarEstado(id, actualizarDTO));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar cita")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar cita")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
