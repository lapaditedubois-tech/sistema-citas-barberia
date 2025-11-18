package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.ProfesionalDTO;
import com.Neita.sistemacitasbarberia.service.ProfesionalService;
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
@RequestMapping("/api/profesionales")
@RequiredArgsConstructor
@Tag(name = "Profesionales", description = "Gestión de profesionales/barberos")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    @GetMapping
    @Operation(summary = "Obtener todos los profesionales")
    public ResponseEntity<List<ProfesionalDTO>> obtenerTodos() {
        return ResponseEntity.ok(profesionalService.obtenerTodos());
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener profesionales activos")
    public ResponseEntity<List<ProfesionalDTO>> obtenerActivos() {
        return ResponseEntity.ok(profesionalService.obtenerActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener profesional por ID")
    public ResponseEntity<ProfesionalDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesionalService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener profesional por ID de usuario")
    public ResponseEntity<ProfesionalDTO> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(profesionalService.obtenerPorUsuarioId(usuarioId));
    }

    @GetMapping("/especialidad/{especialidad}")
    @Operation(summary = "Buscar profesionales por especialidad")
    public ResponseEntity<List<ProfesionalDTO>> obtenerPorEspecialidad(@PathVariable String especialidad) {
        return ResponseEntity.ok(profesionalService.obtenerPorEspecialidad(especialidad));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Obtener profesionales mejor calificados")
    public ResponseEntity<List<ProfesionalDTO>> obtenerMejorCalificados() {
        return ResponseEntity.ok(profesionalService.obtenerMejorCalificados());
    }

    @GetMapping("/servicio/{servicioId}")
    @Operation(summary = "Obtener profesionales que ofrecen un servicio específico")
    public ResponseEntity<List<ProfesionalDTO>> obtenerPorServicio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(profesionalService.obtenerPorServicio(servicioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESIONAL')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nuevo profesional")
    public ResponseEntity<ProfesionalDTO> crear(@Valid @RequestBody ProfesionalDTO.CrearProfesionalDTO crearDTO) {
        ProfesionalDTO profesionalCreado = profesionalService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(profesionalCreado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @profesionalSecurity.esProfesionalActual(#id)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar profesional")
    public ResponseEntity<ProfesionalDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfesionalDTO profesionalDTO) {
        return ResponseEntity.ok(profesionalService.actualizar(id, profesionalDTO));
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Desactivar profesional")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        profesionalService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Activar profesional")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        profesionalService.activar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar profesional")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesionalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
