package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    Optional<Profesional> findByUsuarioId(Long usuarioId);

    List<Profesional> findByActivoTrue();

    List<Profesional> findByEspecialidadContainingIgnoreCaseAndActivoTrue(String especialidad);

    @Query("SELECT p FROM Profesional p WHERE p.activo = true ORDER BY p.calificacionPromedio DESC")
    List<Profesional> findTopRated();

    @Query("SELECT DISTINCT p FROM Profesional p " +
           "JOIN p.servicios bs " +
           "WHERE bs.servicio.id = :servicioId " +
           "AND p.activo = true " +
           "AND bs.disponible = true")
    List<Profesional> findByServicioId(@Param("servicioId") Long servicioId);

    @Query("SELECT COUNT(p) FROM Profesional p WHERE p.activo = true")
    Long contarProfesionalesActivos();

    boolean existsByUsuarioId(Long usuarioId);
}
