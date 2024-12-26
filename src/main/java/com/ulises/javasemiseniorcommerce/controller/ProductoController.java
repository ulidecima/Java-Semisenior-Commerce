package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ApiResponse;
import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ulide
 */
@Tag(name = "Productos", description = "Endpoint para gestionar productos")
@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    /**
     * Obtiene la informacion de un producto a traves de un ID
     * @param id ID del producto
     * @return Informacion del producto
     */
    @Operation(summary = "Obtener producto", description = "Devuelve un producto mediante registrado con el ID proporcionado.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable Long id) {
        logger.info("GET_PRODUCTO_REQUEST: id recibido: {}", id);

        try {
            ProductoDto producto = productoService.getProductoById(id);
            logger.info("GET_PRODUCTO_SUCCESS: Producto obtenido para el ID: {}", id);
            return ResponseEntity.ok(producto);
        } catch (ProductoNotFoundException e) {
            logger.error("GET_PRODUCTO_ERROR: Producto no encontrado con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Producto no encontrado.", false));
        } catch (Exception e) {
            logger.error("GET_PRODUCTO_ERROR: Hubo un error inesperado al obtener el producto con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al obtener el producto.", false));
        }
    }

    /**
     * Obtiene todos los productos y su informacion
     * @return Informacion de todos los productos
     */
    @Operation(summary = "Obtener todos los productos", description = "Devuelve todos los productos y su informacion.")
    @GetMapping
    public ResponseEntity<?> getAllProductos() {
        logger.info("GET_ALL_PRODUCTOS_REQUEST");
        try {
            List<ProductoDto> productos = productoService.getAllProductos();
            logger.info("GET_ALL_PRODUCTOS_SUCCESS: Se listaron {} productos", productos.size());
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            logger.error("GET_ALL_PRODUCTOS_ERROR: Hubo un error al obtener los productos. Error {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error al obtener los productos", false));
        }
    }

    /**
     * Se crea un nuevo producto
     * @param producto Son los datos del producto
     * @return Mensaje de exito
     */
    @Operation(summary = "Crear producto", description = "Crea un producto.")
    @PostMapping
    public ResponseEntity<ApiResponse> createProducto(@Valid @RequestBody ProductoDto producto) {
        logger.info("CREATE_PRODUCTO_REQUEST: Datos recibidos: {}", producto);

        try {
            productoService.createProducto(producto);
            logger.info("CREATE_PRODUCTO_SUCCESS: Producto creado exitosamente: {}", producto.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Producto creado exitosamente", true));
        } catch (IllegalArgumentException e) {
            logger.warn("CREATE_PRODUCTO_ERROR: Datos invalidos para crear el producto {}. Error: {}", producto.getNombre(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Datos invalidos.", false));
        } catch (Exception e) {
            logger.error("CREATE_PRODUCTO_ERROR: Error al crear producto: {}. Error: {}", producto.getNombre(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado al crear el producto.", false));
        }
    }

    /**
     * Actualiza un producto existente
     * @param id ID del producto que se quiere actualiat
     * @param producto Datos actualizados del producto
     * @return Mensaje de exito
     */
    @Operation(summary = "Actualizar producto", description = "Actualiza la informacion de un producto registrado con el ID proporcionado.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDto producto) {
        logger.info("UPDATE_PRODUCTO_REQUES: ID: {}, Datos: {}", id, producto);

        try {
            productoService.updateProducto(id, producto);
            logger.info("UPDATE_PRODUCTO_SUCCESS: Producto actualizado con ID: {}", id);
            return ResponseEntity.ok(new ApiResponse("Producto actualizado correctamente", true));
        } catch (ProductoNotFoundException e) {
            logger.error("UPDATE_PRODUCTO_ERROR: No se ha encontrado producto con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Producto no encontrado.", false));
        } catch (IllegalArgumentException e) {
            logger.error("UPDATE_PRODUCTO_ERROR: Datos invalidos para actualizar el producto con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Argumentos invalidos oara actuaizar el producto.", false));
        } catch (Exception e) {
            logger.error("UPDATE_PRODUCTO_ERROR: Hubo un error inesperado al actualizar el producto con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado al actualizar el producto.", false));
        }
    }

    /**
     * Elimina un producto
     * @param id ID del producto que se quiere eliminar
     * @return Mensaje de exito
     */
    @Operation(summary = "Eliminar producto", description = "Elimina un producto registrado con el ID proporcionado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProducto(@PathVariable Long id) {
        logger.info("DELETE_PRODUCTO_REQUEST: ID recibido: {}", id);

        try {
            productoService.deleteProducto(id);
            logger.info("DELETE_PRODUCTO_SUCCESS: Producto eliminado con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ProductoNotFoundException e) {
            logger.error("DELETE_PRODUCTO_ERROR: Producto no encontrado con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Producto no encontrado.", false));
        } catch (Exception e) {
            logger.error("DELETE_PRODUCTO_ERROR: Error eliminando el producto con ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error inesperado al eliminar el producto.", false));
        }
    }
}
