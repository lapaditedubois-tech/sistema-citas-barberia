package com.Neita.sistemacitasbarberia.security;

import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import com.Neita.sistemacitasbarberia.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("valoracionSecurity")
@RequiredArgsConstructor
public class ValoracionSecurity {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;

    public boolean esPropietarioValoracion(Long valoracionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .flatMap(usuario -> valoracionRepository.findById(valoracionId)
                        .map(valoracion -> valoracion.getUsuario().getId().equals(usuario.getId())))
                .orElse(false);
    }
}
