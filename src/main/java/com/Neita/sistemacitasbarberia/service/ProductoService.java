package com.Neita.sistemacitasbarberia.service;

import com.Neita.sistemacitasbarberia.dto.ProductoDTO;
import com.Neita.sistemacitasbarberia.entity.Producto;
import com.Neita.sistemacitasbarberia.exception.BadRequestException;
import com.Neita.sistemacitasbarberia.exception.ResourceNotFoundException;
import com.Neita.sistemacitasbarberia.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "productos", key = "#id")
    public ProductoDTO obtenerPorId(Long id) {
        logger.debug("Buscando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return convertirADTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los productos");
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerDisponibles() {
        logger.debug("Obteniendo productos disponibles");
        return productoRepository.findByDisponibleTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerEnStock() {
        logger.debug("Obteniendo productos en stock");
        return productoRepository.findProductosEnStock().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        logger.debug("Buscando productos con nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerPorCategoria(String categoria) {
        logger.debug("Obteniendo productos de categoría: {}", categoria);
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "productos", allEntries = true)
    public ProductoDTO crear(ProductoDTO.CrearProductoDTO crearDTO) {
        logger.info("Creando nuevo producto: {}", crearDTO.getNombre());

        Producto producto = Producto.builder()
                .nombre(crearDTO.getNombre())
                .descripcion(crearDTO.getDescripcion())
                .precio(crearDTO.getPrecio())
                .stock(crearDTO.getStock())
                .marca(crearDTO.getMarca())
                .categoria(crearDTO.getCategoria())
                .disponible(crearDTO.getStock() > 0)
                .build();
        
        Producto productoGuardado = productoRepository.save(producto);
        logger.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());
        
        return convertirADTO(productoGuardado);
    }

    @CacheEvict(value = "productos", key = "#id")
    public ProductoDTO actualizar(Long id, ProductoDTO productoDTO) {
        logger.info("Actualizando producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setMarca(productoDTO.getMarca());
        producto.setCategoria(productoDTO.getCategoria());
        
        if (productoDTO.getImagenProducto() != null) {
            producto.setImagenProducto(productoDTO.getImagenProducto());
        }
        
        Producto productoActualizado = productoRepository.save(producto);
        logger.info("Producto actualizado exitosamente");
        
        return convertirADTO(productoActualizado);
    }

    @CacheEvict(value = "productos", key = "#id")
    public ProductoDTO actualizarStock(Long id, ProductoDTO.ActualizarStockDTO actualizarDTO) {
        logger.info("Actualizando stock del producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        if (actualizarDTO.getCantidad() >= 0) {
            producto.aumentarStock(actualizarDTO.getCantidad());
        } else {
            producto.reducirStock(Math.abs(actualizarDTO.getCantidad()));
        }
        
        Producto productoActualizado = productoRepository.save(producto);
        logger.info("Stock actualizado. Nuevo stock: {}", productoActualizado.getStock());
        
        return convertirADTO(productoActualizado);
    }

    @CacheEvict(value = "productos", key = "#id")
    public void cambiarDisponibilidad(Long id, Boolean disponible) {
        logger.info("Cambiando disponibilidad del producto con ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        producto.setDisponible(disponible);
        productoRepository.save(producto);
        
        logger.info("Disponibilidad actualizada a: {}", disponible);
    }

    @CacheEvict(value = "productos", key = "#id")
    public void eliminar(Long id) {
        logger.info("Eliminando producto con ID: {}", id);
        
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        
        productoRepository.deleteById(id);
        logger.info("Producto eliminado exitosamente");
    }

    private ProductoDTO convertirADTO(Producto producto) {
        return modelMapper.map(producto, ProductoDTO.class);
    }
}
