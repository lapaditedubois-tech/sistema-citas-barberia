package com.Neita.sistemacitasbarberia.security;

import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("usuarioSecurity")
@RequiredArgsConstructor
public class UsuarioSecurity {

    private final UsuarioRepository usuarioRepository;

    public boolean esUsuarioActual(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .map(usuario -> usuario.getId().equals(usuarioId))
                .orElse(false);
    }

    public boolean esPropietarioCita(Long citaId) {
        // Implementación para verificar si el usuario es propietario de la cita
        return true; // Simplificado por ahora
    }
}
