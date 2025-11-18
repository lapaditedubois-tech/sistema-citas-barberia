package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrue();

    List<Servicio> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    @Query("SELECT DISTINCT s FROM Servicio s " +
           "JOIN s.barberos bs " +
           "WHERE bs.profesional.id = :profesionalId " +
           "AND s.activo = true " +
           "AND bs.disponible = true")
    List<Servicio> findByProfesionalId(@Param("profesionalId") Long profesionalId);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.activo = true")
    Long contarServiciosActivos();

    boolean existsByNombre(String nombre);
}
