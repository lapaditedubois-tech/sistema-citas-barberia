package com.Neita.sistemacitasbarberia.controller;

import com.Neita.sistemacitasbarberia.dto.ProductoDTO;
import com.Neita.sistemacitasbarberia.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos de barbería")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener productos disponibles")
    public ResponseEntity<List<ProductoDTO>> obtenerDisponibles() {
        return ResponseEntity.ok(productoService.obtenerDisponibles());
    }

    @GetMapping("/en-stock")
    @Operation(summary = "Obtener productos en stock")
    public ResponseEntity<List<ProductoDTO>> obtenerEnStock() {
        return ResponseEntity.ok(productoService.obtenerEnStock());
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener todos los productos")
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener productos por categoría")
    public ResponseEntity<List<ProductoDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nuevo producto")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO.CrearProductoDTO crearDTO) {
        ProductoDTO productoCreado = productoService.crear(crearDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        return ResponseEntity.ok(productoService.actualizar(id, productoDTO));
    }

    @PutMapping("/{id}/stock")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar stock de producto")
    public ResponseEntity<ProductoDTO> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO.ActualizarStockDTO actualizarDTO) {
        return ResponseEntity.ok(productoService.actualizarStock(id, actualizarDTO));
    }

    @PutMapping("/{id}/disponibilidad")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar disponibilidad de producto")
    public ResponseEntity<Void> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        productoService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
