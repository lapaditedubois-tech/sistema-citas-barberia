package com.Neita.sistemacitasbarberia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "barbero_servicio", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"profesional_id", "servicio_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    @Column(columnDefinition = "TEXT")
    private String notasEspeciales;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Método helper para obtener precio formateado en COP
    public String getPrecioFormateado() {
        return String.format("$%,.0f COP", precio);
    }
}
