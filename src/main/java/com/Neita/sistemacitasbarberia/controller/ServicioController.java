package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.ServicioDTO;
import com.Neita.sistemacitasbarberia.service.ServicioService;
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
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Gestión de servicios de barbería")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    @Operation(summary = "Obtener todos los servicios")
    public ResponseEntity<List<ServicioDTO>> obtenerTodos() {
        return ResponseEntity.ok(servicioService.obtenerTodos());
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener servicios activos")
    public ResponseEntity<List<ServicioDTO>> obtenerActivos() {
        return ResponseEntity.ok(servicioService.obtenerActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID")
    public ResponseEntity<ServicioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar servicios por nombre")
    public ResponseEntity<List<ServicioDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(servicioService.buscarPorNombre(nombre));
    }

    @GetMapping("/profesional/{profesionalId}")
    @Operation(summary = "Obtener servicios de un profesional")
    public ResponseEntity<List<ServicioDTO>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(servicioService.obtenerPorProfesional(profesionalId));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nuevo servicio")
    public ResponseEntity<ServicioDTO> crear(@Valid @RequestBody ServicioDTO servicioDTO) {
        ServicioDTO servicioCreado = servicioService.crear(servicioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioCreado);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar servicio")
    public ResponseEntity<ServicioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioDTO servicioDTO) {
        return ResponseEntity.ok(servicioService.actualizar(id, servicioDTO));
    }

    @PutMapping("/{id}/desactivar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Desactivar servicio")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        servicioService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Activar servicio")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        servicioService.activar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar servicio")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
