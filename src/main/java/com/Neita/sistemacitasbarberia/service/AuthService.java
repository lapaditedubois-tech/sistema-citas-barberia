package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.AuthDTO;
import com.Neita.sistemacitasbarberia.dto.UsuarioDTO;
import com.Neita.sistemacitasbarberia.entity.Usuario;
import com.Neita.sistemacitasbarberia.repository.UsuarioRepository;
import com.Neita.sistemacitasbarberia.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthDTO.AuthResponseDTO login(AuthDTO.LoginDTO loginDTO) {
        logger.info("Intento de login para usuario: {}", loginDTO.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getPassword()
            )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow();
        
        String[] roles = usuario.getRoles().toArray(new String[0]);
        
        logger.info("Login exitoso para usuario: {}", loginDTO.getEmail());
        
        return AuthDTO.AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .roles(roles)
                .build();
    }

    public AuthDTO.AuthResponseDTO registro(UsuarioDTO.RegistroDTO registroDTO) {
        logger.info("Registro de nuevo usuario: {}", registroDTO.getEmail());
        
        UsuarioDTO usuarioCreado = usuarioService.registrar(registroDTO);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(registroDTO.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        
        Usuario usuario = usuarioRepository.findByEmail(registroDTO.getEmail())
                .orElseThrow();
        
        String[] roles = usuario.getRoles().toArray(new String[0]);
        
        logger.info("Registro exitoso para usuario: {}", registroDTO.getEmail());
        
        return AuthDTO.AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .roles(roles)
                .build();
    }
}
