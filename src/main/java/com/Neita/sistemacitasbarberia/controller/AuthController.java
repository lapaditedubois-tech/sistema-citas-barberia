package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.AuthDTO;
import com.Neita.sistemacitasbarberia.dto.UsuarioDTO;
import com.Neita.sistemacitasbarberia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    public ResponseEntity<AuthDTO.AuthResponseDTO> login(@Valid @RequestBody AuthDTO.LoginDTO loginDTO) {
        AuthDTO.AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario")
    public ResponseEntity<AuthDTO.AuthResponseDTO> registro(@Valid @RequestBody UsuarioDTO.RegistroDTO registroDTO) {
        AuthDTO.AuthResponseDTO response = authService.registro(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
