package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDTO {

    // DTO para login
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    // DTO para respuesta de autenticación
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponseDTO {
        private String token;
        private String tipo = "Bearer";
        private Long usuarioId;
        private String nombre;
        private String email;
        private String[] roles;
    }
}
