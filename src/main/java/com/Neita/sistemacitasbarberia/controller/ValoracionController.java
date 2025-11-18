package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.ValoracionDTO;
import com.Neita.sistemacitasbarberia.service.ValoracionService;
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
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
@Tag(name = "Valoraciones", description = "Gestión de valoraciones y reseñas")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener valoración por ID")
    public ResponseEntity<ValoracionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(valoracionService.obtenerPorId(id));
    }

    @GetMapping("/profesional/{profesionalId}")
    @Operation(summary = "Obtener valoraciones de un profesional")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(valoracionService.obtenerPorProfesional(profesionalId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.esUsuarioActual(#usuarioId)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener valoraciones de un usuario")
    public ResponseEntity<List<ValoracionDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(valoracionService.obtenerPorUsuario(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nueva valoración")
    public ResponseEntity<ValoracionDTO> crear(@Valid @RequestBody ValoracionDTO.CrearValoracionDTO crearDTO) {
        ValoracionDTO valoracionCreada = valoracionService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(valoracionCreada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @valoracionSecurity.esPropietarioValoracion(#id)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar valoración")
    public ResponseEntity<ValoracionDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ValoracionDTO valoracionDTO) {
        return ResponseEntity.ok(valoracionService.actualizar(id, valoracionDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @valoracionSecurity.esPropietarioValoracion(#id)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar valoración")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        valoracionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
