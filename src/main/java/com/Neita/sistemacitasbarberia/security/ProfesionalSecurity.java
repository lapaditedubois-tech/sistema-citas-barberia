package com.Neita.sistemacitasbarberia.security;

import com.Neita.sistemacitasbarberia.repository.ProfesionalRepository;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("profesionalSecurity")
@RequiredArgsConstructor
public class ProfesionalSecurity {

    private final ProfesionalRepository profesionalRepository;
    private final UsuarioRepository usuarioRepository;

    public boolean esProfesionalActual(Long profesionalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .flatMap(usuario -> profesionalRepository.findByUsuarioId(usuario.getId()))
                .map(profesional -> profesional.getId().equals(profesionalId))
                .orElse(false);
    }
}
