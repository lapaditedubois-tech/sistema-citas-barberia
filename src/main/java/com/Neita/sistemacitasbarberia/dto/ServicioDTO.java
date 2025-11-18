package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioDTO {

    private Long id;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "La duración es obligatoria")
    @Pattern(regexp = "^\\d+\\s*(min|minutos|hora|horas)$", 
            message = "La duración debe tener formato válido (ej: 30 min, 1 hora)")
    private String duracion;

    private String imagenServicio;

    private Boolean activo;

    // DTO simplificado para listados
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicioSimpleDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String duracion;
        private String imagenServicio;
    }
}
