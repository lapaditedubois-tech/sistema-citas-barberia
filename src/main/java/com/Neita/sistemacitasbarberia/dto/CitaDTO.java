package com.Neita.sistemacitasbarberia.dto;

import com.Neita.sistemacitasbarberia.entity.Cita.EstadoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaDTO {

    private Long id;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime fechaHora;

    private EstadoCita estado;

    private String notas;

    private Double precioFinal;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCompletada;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Long profesionalId;

    // Información adicional para vistas
    private String nombreUsuario;
    private String emailUsuario;
    private String telefonoUsuario;
    private String nombreServicio;
    private String duracionServicio;
    private String nombreProfesional;
    private String especialidadProfesional;

    // DTO para crear cita
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearCitaDTO {
        
        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La fecha debe ser futura")
        private LocalDateTime fechaHora;

        private String notas;

        @NotNull(message = "El ID del usuario es obligatorio")
        private Long usuarioId;

        @NotNull(message = "El ID del servicio es obligatorio")
        private Long servicioId;

        @NotNull(message = "El ID del profesional es obligatorio")
        private Long profesionalId;
    }

    // DTO para actualizar estado
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarEstadoDTO {
        
        @NotNull(message = "El estado es obligatorio")
        private EstadoCita estado;

        private String notas;
    }

    // DTO simplificado para listados
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CitaSimpleDTO {
        private Long id;
        private LocalDateTime fechaHora;
        private EstadoCita estado;
        private String nombreUsuario;
        private String nombreServicio;
        private String nombreProfesional;
        private Double precioFinal;
    }
}
