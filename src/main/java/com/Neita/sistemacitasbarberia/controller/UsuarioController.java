package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.UsuarioDTO;
import com.Neita.sistemacitasbarberia.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.esUsuarioActual(#id)")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios activos")
    public ResponseEntity<List<UsuarioDTO>> obtenerActivos() {
        return ResponseEntity.ok(usuarioService.obtenerActivos());
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios por rol")
    public ResponseEntity<List<UsuarioDTO>> obtenerPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.obtenerPorRol(rol));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.esUsuarioActual(#id)")
    @Operation(summary = "Actualizar perfil de usuario")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.ActualizarPerfilDTO actualizarDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, actualizarDTO));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.esUsuarioActual(#id)")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.CambiarPasswordDTO cambiarPasswordDTO) {
        usuarioService.cambiarPassword(id, cambiarPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar usuario")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar usuario")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles/{rol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar rol a usuario")
    public ResponseEntity<Void> agregarRol(@PathVariable Long id, @PathVariable String rol) {
        usuarioService.agregarRol(id, rol);
        return ResponseEntity.ok().build();
    }
}
