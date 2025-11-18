package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoracionDTO {

    private Long id;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    private String comentario;

    private LocalDateTime fechaValoracion;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Long profesionalId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private Long citaId;

    // Información adicional
    private String nombreUsuario;
    private String nombreProfesional;

    // DTO para crear valoración
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearValoracionDTO {
        
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        private Integer calificacion;

        private String comentario;

        @NotNull(message = "El ID del profesional es obligatorio")
        private Long profesionalId;

        @NotNull(message = "El ID del usuario es obligatorio")
        private Long usuarioId;

        private Long citaId;
    }
}
