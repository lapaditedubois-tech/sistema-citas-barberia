package com.Neita.sistemacitasbarberia.security;

import com.Neita.sistemacitasbarberia.repository.CitaRepository;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("citaSecurity")
@RequiredArgsConstructor
public class CitaSecurity {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    public boolean esPropietarioCita(Long citaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .flatMap(usuario -> citaRepository.findById(citaId)
                        .map(cita -> cita.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }
}
