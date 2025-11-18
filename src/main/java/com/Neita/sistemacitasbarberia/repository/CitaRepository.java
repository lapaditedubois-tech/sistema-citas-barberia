package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Cita;
import com.Neita.sistemacitasbarberia.entity.Cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByUsuarioId(Long usuarioId);

    List<Cita> findByProfesionalId(Long profesionalId);

    List<Cita> findByEstado(EstadoCita estado);

    @Query("SELECT c FROM Cita c WHERE c.usuario.id = :usuarioId AND c.estado = :estado")
    List<Cita> findByUsuarioIdAndEstado(@Param("usuarioId") Long usuarioId, 
                                        @Param("estado") EstadoCita estado);

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :profesionalId AND c.estado = :estado")
    List<Cita> findByProfesionalIdAndEstado(@Param("profesionalId") Long profesionalId, 
                                            @Param("estado") EstadoCita estado);

    @Query("SELECT c FROM Cita c " +
           "WHERE c.profesional.id = :profesionalId " +
           "AND c.fechaHora BETWEEN :inicio AND :fin " +
           "AND c.estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO')")
    List<Cita> findCitasActivasByProfesionalAndFecha(@Param("profesionalId") Long profesionalId,
                                                      @Param("inicio") LocalDateTime inicio,
                                                      @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Cita c " +
           "WHERE c.usuario.id = :usuarioId " +
           "AND c.fechaHora >= :fechaActual " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findProximasCitasByUsuario(@Param("usuarioId") Long usuarioId,
                                          @Param("fechaActual") LocalDateTime fechaActual);

    @Query("SELECT c FROM Cita c " +
           "WHERE c.profesional.id = :profesionalId " +
           "AND c.fechaHora >= :fechaActual " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findProximasCitasByProfesional(@Param("profesionalId") Long profesionalId,
                                               @Param("fechaActual") LocalDateTime fechaActual);

    @Query("SELECT c FROM Cita c " +
           "WHERE c.usuario.id = :usuarioId " +
           "AND c.estado = 'COMPLETADA' " +
           "ORDER BY c.fechaCompletada DESC")
    List<Cita> findHistorialByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado")
    Long contarPorEstado(@Param("estado") EstadoCita estado);

    @Query("SELECT COUNT(c) FROM Cita c " +
           "WHERE c.profesional.id = :profesionalId " +
           "AND c.estado = 'COMPLETADA'")
    Long contarCitasCompletadasByProfesional(@Param("profesionalId") Long profesionalId);
}
