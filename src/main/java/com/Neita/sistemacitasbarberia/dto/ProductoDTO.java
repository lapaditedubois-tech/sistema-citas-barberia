package com.Neita.sistemacitasbarberia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private String imagenProducto;

    private Boolean disponible;

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
    private String categoria;

    private LocalDateTime fechaCreacion;

    // DTO para crear producto
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrearProductoDTO {
        
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        private String nombre;

        private String descripcion;

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private Double precio;

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        private Integer stock;

        @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
        private String marca;

        @Size(max = 100, message = "La categoría no puede exceder 100 caracteres")
        private String categoria;
    }

    // DTO para actualizar stock
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarStockDTO {
        
        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;
    }
}
