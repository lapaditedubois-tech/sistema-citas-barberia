package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.GaleriaDTO;
import com.Neita.sistemacitasbarberia.service.GaleriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/galeria")
@RequiredArgsConstructor
@Tag(name = "Galería", description = "Gestión de galería de trabajos")
public class GaleriaController {

    private final GaleriaService galeriaService;

    @GetMapping("/visibles")
    @Operation(summary = "Obtener todas las entradas visibles de galería")
    public ResponseEntity<List<GaleriaDTO>> obtenerTodasVisibles() {
        return ResponseEntity.ok(galeriaService.obtenerTodasVisibles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrada de galería por ID")
    public ResponseEntity<GaleriaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(galeriaService.obtenerPorId(id));
    }

    @GetMapping("/profesional/{profesionalId}")
    @Operation(summary = "Obtener galería de un profesional")
    public ResponseEntity<List<GaleriaDTO>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(galeriaService.obtenerPorProfesional(profesionalId));
    }

    @GetMapping("/profesional/{profesionalId}/visibles")
    @Operation(summary = "Obtener galería visible de un profesional")
    public ResponseEntity<List<GaleriaDTO>> obtenerVisiblesPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(galeriaService.obtenerVisiblesPorProfesional(profesionalId));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear entrada de galería")
    public ResponseEntity<GaleriaDTO> crear(@Valid @RequestBody GaleriaDTO.CrearGaleriaDTO crearDTO) {
        GaleriaDTO galeriaCreada = galeriaService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(galeriaCreada);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar entrada de galería")
    public ResponseEntity<GaleriaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GaleriaDTO galeriaDTO) {
        return ResponseEntity.ok(galeriaService.actualizar(id, galeriaDTO));
    }

    @PutMapping("/{id}/visibilidad")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar visibilidad de entrada de galería")
    public ResponseEntity<Void> cambiarVisibilidad(
            @PathVariable Long id,
            @RequestParam Boolean visible) {
        galeriaService.cambiarVisibilidad(id, visible);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar entrada de galería")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        galeriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
