package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Galeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GaleriaRepository extends JpaRepository<Galeria, Long> {

    List<Galeria> findByProfesionalId(Long profesionalId);

    @Query("SELECT g FROM Galeria g " +
           "WHERE g.profesional.id = :profesionalId " +
           "AND g.visible = true " +
           "ORDER BY g.fechaSubida DESC")
    List<Galeria> findVisiblesByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT g FROM Galeria g " +
           "WHERE g.visible = true " +
           "ORDER BY g.fechaSubida DESC")
    List<Galeria> findAllVisibles();

    @Query("SELECT COUNT(g) FROM Galeria g WHERE g.profesional.id = :profesionalId")
    Long contarPorProfesional(@Param("profesionalId") Long profesionalId);
}
