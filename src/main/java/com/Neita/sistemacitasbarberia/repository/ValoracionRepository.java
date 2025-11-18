package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByProfesionalId(Long profesionalId);

    List<Valoracion> findByUsuarioId(Long usuarioId);

    Optional<Valoracion> findByCitaId(Long citaId);

    @Query("SELECT v FROM Valoracion v " +
           "WHERE v.profesional.id = :profesionalId " +
           "ORDER BY v.fechaValoracion DESC")
    List<Valoracion> findByProfesionalIdOrderByFechaDesc(@Param("profesionalId") Long profesionalId);

    @Query("SELECT AVG(v.calificacion) FROM Valoracion v WHERE v.profesional.id = :profesionalId")
    Double calcularPromedioByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.profesional.id = :profesionalId")
    Long contarValoracionesByProfesional(@Param("profesionalId") Long profesionalId);

    boolean existsByCitaId(Long citaId);
}
