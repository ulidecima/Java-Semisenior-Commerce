package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ulide
 */
@Service
@AllArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    /**
     * Crea un producto nuevo
     * @param productoDto Datos del producto que se va a crear
     */
    @Transactional
    public void createProducto(ProductoDto productoDto) {
        logger.info("Creando producto: {}", productoDto.getNombre());

        validateData(productoDto);

        try {
            ProductoModel producto = ProductoModel.builder()
                    .nombre(productoDto.getNombre())
                    .descripcion(productoDto.getDescripcion())
                    .precio(productoDto.getPrecio())
                    .stockDisponible(productoDto.getStockDisponible())
                    .build();

            productoRepository.save(producto);
            logger.info("Producto creado correctamente: {}", producto.getNombre());
        } catch (Exception e) {
            logger.error("Error al crear el producto: {}. Error: {}", productoDto.getNombre(), e.getMessage());
            throw new RuntimeException("Error al crear el producto.", e);
        }
    }

    /**
     * Obtiene un produco por su ID
     * @param id ID del producto
     * @return productoDto con los datos del producto
     */
    public ProductoDto getProductoById(Long id) {
        logger.info("Buscando producto con ID: {}", id);
        ProductoModel productoModel = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        logger.info("Producto encontrado: {}", productoModel.getNombre());
        return mapToDto(productoModel);
    }

    /**
     * Obtiene todos los productos
     * @return Lista de productos
     */
    public List<ProductoDto> getAllProductos() {
        logger.info("Obteniendo todos los productos...");
        List<ProductoDto> productos = productoRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
        logger.info("Total de productos encontrados: {}", productos.size());
        return productos;
    }

    /**
     * Actualiza un producto existente
     * @param id ID del producto que se quiere actualizar
     * @param productoDto Dato del producto actualizado
     */
    @Transactional
    public void updateProducto(Long id, ProductoDto productoDto) {
        logger.info("Actualizando producto con ID: {}", id);
        ProductoModel productoModel = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        validateData(productoDto);

        try {
            productoModel.setNombre(productoDto.getNombre());
            productoModel.setDescripcion(productoDto.getDescripcion());
            productoModel.setPrecio(productoDto.getPrecio());
            productoModel.setStockDisponible(productoDto.getStockDisponible());

            productoRepository.save(productoModel);
            logger.info("Producto actualizado correctamente: {}", productoModel.getNombre());
        } catch (Exception e) {
            logger.error("Error al actualizar el producto con ID: {}. Error: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar el producto.", e);
        }
    }

    /**
     * Elimina un producto mediante su ID
     * @param id ID del producto
     */
    @Transactional
    public void deleteProducto(Long id) {
        logger.info("Eliminando producto con ID: {}", id);
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        try {
            productoRepository.delete(producto);
            logger.info("Producto eliminado correctamente: {}", producto.getNombre());
        } catch (Exception e) {
            logger.error("Error al eliminar el producto con ID: {}, Error: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar el producto.", e);
        }
    }

    private ProductoDto mapToDto(ProductoModel producto) {
        return ProductoDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stockDisponible(producto.getStockDisponible())
                .build();
    }

    private void validateData(ProductoDto productoDto) {
        if (productoDto.getPrecio() <= 0) {
            logger.warn("Precio invalido para el producto: {}. Precio recibido: {}", productoDto.getNombre(), productoDto.getPrecio());
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        if (productoDto.getStockDisponible() <= 0) {
            logger.warn("Stock invalido para el producto: {}. Stock recibido: {}", productoDto.getNombre(), productoDto.getStockDisponible());
            throw new IllegalArgumentException("El stock debe ser mayor o igual que cero.");
        }
    }
}
