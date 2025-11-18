package com.Neita.sistemacitasbarberia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "imagen_producto")
    private String imagenProducto;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String categoria;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Método helper para verificar disponibilidad
    public boolean estaDisponible() {
        return disponible && stock > 0;
    }

    // Método helper para reducir stock
    public void reducirStock(int cantidad) {
        if (stock >= cantidad) {
            this.stock -= cantidad;
            if (this.stock == 0) {
                this.disponible = false;
            }
        } else {
            throw new IllegalStateException("Stock insuficiente");
        }
    }

    // Método helper para aumentar stock
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
        if (this.stock > 0) {
            this.disponible = true;
        }
    }

    // Método helper para precio formateado
    public String getPrecioFormateado() {
        return String.format("$%,.0f COP", precio);
    }
}
