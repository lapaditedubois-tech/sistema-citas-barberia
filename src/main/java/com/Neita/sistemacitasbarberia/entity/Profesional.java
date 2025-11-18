package com.Neita.sistemacitasbarberia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profesional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String especialidad;

    @Column(name = "horario_disponible", length = 500)
    private String horarioDisponible;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Cita> citas = new HashSet<>();

    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BarberoServicio> servicios = new HashSet<>();

    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Valoracion> valoraciones = new HashSet<>();

    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Galeria> galeria = new HashSet<>();

    // Método helper para calcular calificación promedio
    public void actualizarCalificacionPromedio() {
        if (valoraciones == null || valoraciones.isEmpty()) {
            this.calificacionPromedio = 0.0;
            return;
        }
        
        double suma = valoraciones.stream()
                .mapToDouble(Valoracion::getCalificacion)
                .sum();
        this.calificacionPromedio = suma / valoraciones.size();
    }
}
