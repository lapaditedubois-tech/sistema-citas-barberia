package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.BarberoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberoServicioRepository extends JpaRepository<BarberoServicio, Long> {

    List<BarberoServicio> findByProfesionalId(Long profesionalId);

    List<BarberoServicio> findByServicioId(Long servicioId);

    Optional<BarberoServicio> findByProfesionalIdAndServicioId(Long profesionalId, Long servicioId);

    @Query("SELECT bs FROM BarberoServicio bs " +
           "WHERE bs.profesional.id = :profesionalId " +
           "AND bs.disponible = true")
    List<BarberoServicio> findServiciosDisponiblesByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT bs FROM BarberoServicio bs " +
           "WHERE bs.servicio.id = :servicioId " +
           "AND bs.disponible = true " +
           "AND bs.profesional.activo = true " +
           "ORDER BY bs.precio ASC")
    List<BarberoServicio> findProfesionalesDisponiblesByServicio(@Param("servicioId") Long servicioId);

    boolean existsByProfesionalIdAndServicioId(Long profesionalId, Long servicioId);

    @Query("SELECT COUNT(bs) FROM BarberoServicio bs WHERE bs.profesional.id = :profesionalId")
    Long contarServiciosPorProfesional(@Param("profesionalId") Long profesionalId);
}
