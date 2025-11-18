package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesionalDTO {

    private Long id;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 255, message = "La especialidad no puede exceder 255 caracteres")
    private String especialidad;

    @Size(max = 500, message = "El horario disponible no puede exceder 500 caracteres")
    private String horarioDisponible;

    private String biografia;

    private String fotoPerfil;

    private Boolean activo;

    private Double calificacionPromedio;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String nombreUsuario;
    private String emailUsuario;
    private String telefonoUsuario;

    // DTO simplificado para listados
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfesionalSimpleDTO {
        private Long id;
        private String nombre;
        private String especialidad;
        private String fotoPerfil;
        private Double calificacionPromedio;
        private Boolean activo;
    }

    // DTO para crear profesional
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearProfesionalDTO {
        
        @NotNull(message = "El ID del usuario es obligatorio")
        private Long usuarioId;

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 255, message = "La especialidad no puede exceder 255 caracteres")
        private String especialidad;

        @Size(max = 500, message = "El horario disponible no puede exceder 500 caracteres")
        private String horarioDisponible;

        private String biografia;
    }
}
