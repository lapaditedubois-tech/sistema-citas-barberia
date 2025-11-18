package com.Neita.sistemacitasbarberia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "precio_final")
    private Double precioFinal;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    // Enum para estados de cita
    public enum EstadoCita {
        PENDIENTE,
        CONFIRMADA,
        EN_PROCESO,
        COMPLETADA,
        CANCELADA,
        NO_ASISTIO
    }

    // Método helper para verificar si la cita puede ser modificada
    public boolean puedeSerModificada() {
        return estado == EstadoCita.PENDIENTE || estado == EstadoCita.CONFIRMADA;
    }

    // Método helper para verificar si la cita está activa
    public boolean estaActiva() {
        return estado != EstadoCita.CANCELADA && estado != EstadoCita.COMPLETADA && estado != EstadoCita.NO_ASISTIO;
    }
}
