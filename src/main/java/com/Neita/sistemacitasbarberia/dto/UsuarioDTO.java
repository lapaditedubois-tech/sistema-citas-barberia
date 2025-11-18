package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener solo números y tener entre 7 y 20 dígitos")
    private String telefono;

    private LocalDateTime fechaRegistro;

    private Boolean activo;

    private Set<String> roles;

    // DTO para registro (incluye password)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistroDTO {
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
        private String nombre;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
        private String password;

        @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener solo números")
        private String telefono;
    }

    // DTO para actualización de perfil
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarPerfilDTO {
        
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
        private String nombre;

        @Pattern(regexp = "^[0-9]{7,20}$", message = "El teléfono debe contener solo números")
        private String telefono;
    }

    // DTO para cambio de contraseña
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CambiarPasswordDTO {
        
        @NotBlank(message = "La contraseña actual es obligatoria")
        private String passwordActual;

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
        private String passwordNueva;
    }
}
