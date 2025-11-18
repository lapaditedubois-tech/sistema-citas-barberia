package com.Neita.sistemacitasbarberia.repository;

import com.Neita.sistemacitasbarberia.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByMarca(String marca);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND p.stock > 0")
    List<Producto> findProductosEnStock();

    @Query("SELECT p FROM Producto p WHERE p.stock <= :cantidad")
    List<Producto> findProductosBajoStock(@Param("cantidad") Integer cantidad);

    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL")
    List<String> findAllCategorias();

    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.marca IS NOT NULL")
    List<String> findAllMarcas();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.disponible = true")
    Long contarProductosDisponibles();
}
