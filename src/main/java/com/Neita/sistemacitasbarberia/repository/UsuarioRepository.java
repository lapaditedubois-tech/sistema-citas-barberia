package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByActivoTrue();

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND :rol MEMBER OF u.roles")
    List<Usuario> findByRol(@Param("rol") String rol);

    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.activo = true")
    List<Usuario> buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE :rol MEMBER OF u.roles")
    Long contarPorRol(@Param("rol") String rol);
}
