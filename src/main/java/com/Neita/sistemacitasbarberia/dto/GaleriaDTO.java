package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GaleriaDTO {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String titulo;

    private String descripcion;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String urlImagen;

    private LocalDateTime fechaSubida;

    private Boolean visible;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Long profesionalId;

    private String nombreProfesional;

    // DTO para crear entrada de galería
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearGaleriaDTO {
        
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede exceder 255 caracteres")
        private String titulo;

        private String descripcion;

        @NotBlank(message = "La URL de la imagen es obligatoria")
        private String urlImagen;

        @NotNull(message = "El ID del profesional es obligatorio")
        private Long profesionalId;
    }
}
