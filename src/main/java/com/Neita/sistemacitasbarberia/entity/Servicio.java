package com.Neita.sistemacitasbarberia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String duracion;

    @Column(name = "imagen_servicio")
    private String imagenServicio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BarberoServicio> barberos = new HashSet<>();

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Cita> citas = new HashSet<>();
}
