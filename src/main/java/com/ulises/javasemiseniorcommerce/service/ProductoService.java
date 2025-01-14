package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.dto.ProductoRequest;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
     *
     * @param productoRequest Datos del producto que se va a crear
     */
    @Transactional
    public ProductoDto createProducto(ProductoRequest productoRequest) {
        logger.info("Creando producto: {}", productoRequest.getNombre());

        validateData(productoRequest);

        ProductoModel producto = ProductoModel.builder()
                .nombre(productoRequest.getNombre())
                .descripcion(productoRequest.getDescripcion())
                .precio(productoRequest.getPrecio())
                .stockDisponible(productoRequest.getStockDisponible())
                .build();

        productoRepository.save(producto);
        logger.info("Producto creado correctamente: {}", productoRequest.getNombre());
        return mapToDto(producto);
    }

    /**
     * Obtiene un produco por su ID
     *
     * @param id ID del producto
     * @return productoDto con los datos del producto
     */
    public ProductoDto getProductoById(Long id) {
        logger.info("Buscando producto con ID: {}", id);
        ProductoModel productoModel = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    // Excepcion por si no se encuentra el producto
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        logger.info("Producto encontrado: {}", productoModel.getNombre());
        return mapToDto(productoModel);
    }

    /**
     * Obtiene todos los productos
     *
     * @return Lista de productos
     */
    public Page<ProductoDto> getAllProductos(Double precioMin, Double precioMax, int page, int size) {
        logger.info("Obteniendo todos los productos...");

        // Se validan los valores de los filtros de precio
        validatePrecios(precioMin, precioMax);

        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findAll(pageable).map(this::mapToDto);
    }

    /**
     * Actualiza un producto existente
     *
     * @param id          ID del producto que se quiere actualizar
     * @param productoRequest Dato del producto actualizado
     */
    @Transactional
    public ProductoDto updateProducto(Long id, ProductoRequest productoRequest) {
        logger.info("Actualizando producto con ID: {}", id);
        ProductoModel productoModel = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", id);
                    // Excepcion por si no se encuentra el producto
                    return new ProductoNotFoundException("Producto no encontrado con ID: " + id);
                });

        // Se validan los datos ingresados
        validateData(productoRequest);

        // Se actualizan valores
        productoModel.setNombre(productoRequest.getNombre());
        productoModel.setDescripcion(productoRequest.getDescripcion());
        productoModel.setPrecio(productoRequest.getPrecio());
        productoModel.setStockDisponible(productoRequest.getStockDisponible());

        productoRepository.save(productoModel);
        logger.info("Producto actualizado correctamente: {}", productoModel.getNombre());
        return mapToDto(productoModel);
    }

    /**
     * Elimina un producto mediante su ID. Cuando se elimina un producto,
     * las referencias se actualizan a null para el caso de ID, a 0.0
     * o null en caso de los precios, o un mensaje de no disponibilidad segun corresponda.
     *
     * @param id ID del producto
     */
    @Transactional
    public void deleteProducto(Long id) {
        logger.info("Eliminando producto con ID: {}", id);

        productoRepository.findById(id).ifPresent(productoRepository::delete);
        logger.info("Producto eliminado correctamente.");
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

    // Metodo para validar los valores de los precios
    private void validatePrecios(Double precioMin, Double precioMax) {
        // Excepcion por si el precio minimo es mayor que el precio maximo
        if (precioMin != null && precioMax != null && precioMin > precioMax)
            throw new IllegalArgumentException("El precio minimo no puede ser mayor que el precio maximo.");

        // Excepcion por si los filtros de precio son menores o iguales que cero
        if ((precioMin != null && precioMin <= 0) || (precioMax != null && precioMax <= 0))
            throw new IllegalArgumentException("Los filtros de precio deben ser mayores que cero.");
    }

    // Metodo para validar que los valores de precio y stock sean adecuados
    private void validateData(ProductoRequest productoRequest) {
        if (productoRequest.getPrecio() <= 0) {
            logger.warn("Precio invalido para el producto: {}. Precio recibido: {}", productoRequest.getNombre(), productoRequest.getPrecio());
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        if (productoRequest.getStockDisponible() <= 0) {
            logger.warn("Stock invalido para el producto: {}. Stock recibido: {}", productoRequest.getNombre(), productoRequest.getStockDisponible());
            throw new IllegalArgumentException("El stock debe ser mayor o igual que cero.");
        }
    }

    /**
     * Busca productos en base a los filtros dados. En caso de no dar valor a los filtros, retorna todos los productos.
     * @param nombreProducto Nombre del producto que se quiere buscar.
     * @param precioMin Precio minimo del producto.
     * @param precioMax Precio maximo del producto.
     * @param page Numero de pagina.
     * @param size Tamanio de la muestra de la pagina.
     * @return Page<ProductoDto> Pagina con los productos encontrados.
     */
    public Page<ProductoDto> getProductosBySearch(String nombreProducto, Double precioMin, Double precioMax, int page, int size) {

        // Se validan los valores de los filtros de precio
        validatePrecios(precioMin, precioMax);

        // En caso de no especificar palabras clave para la busqueda, se llama al metodo getAllProductos
        // con los filtros de precio (si aplican) y los datos de page y size
        if (nombreProducto == null || nombreProducto.isBlank()) {
            return getAllProductos(precioMin, precioMax, page, size);
        }

        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.
                searchProductosByPalabrasClave(nombreProducto, precioMin, precioMax, pageable)
                .map(this::mapToDto);
    }
}
