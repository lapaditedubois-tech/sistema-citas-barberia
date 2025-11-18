package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.UsuarioDTO;
import com.Neita.sistemacitasbarberia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener usuarios activos")
    public ResponseEntity<List<UsuarioDTO>> obtenerActivos() {
        return ResponseEntity.ok(usuarioService.obtenerActivos());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil de usuario")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.ActualizarPerfilDTO actualizarDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, actualizarDTO));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.CambiarPasswordDTO cambiarPasswordDTO) {
        usuarioService.cambiarPassword(id, cambiarPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar usuario")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar usuario")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
