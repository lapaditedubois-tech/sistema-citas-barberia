package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberoServicioDTO {

    private Long id;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Long profesionalId;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

    private Boolean disponible;

    private String notasEspeciales;

    private LocalDateTime fechaCreacion;

    // Información adicional para vistas
    private String nombreServicio;
    private String descripcionServicio;
    private String duracionServicio;
    private String nombreProfesional;
    private String especialidadProfesional;

    // DTO para crear relación barbero-servicio
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearBarberoServicioDTO {
        
        @NotNull(message = "El ID del profesional es obligatorio")
        private Long profesionalId;

        @NotNull(message = "El ID del servicio es obligatorio")
        private Long servicioId;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private Double precio;

        private String notasEspeciales;
    }

    // DTO para actualizar precio
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarPrecioDTO {
        
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private Double precio;

        private String notasEspeciales;
    }
}
