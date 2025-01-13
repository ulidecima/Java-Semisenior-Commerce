package com.ulises.javasemiseniorcommerce.controller;

import com.ulises.javasemiseniorcommerce.dto.ProductoRequest;
import com.ulises.javasemiseniorcommerce.dto.ProductoDto;
import com.ulises.javasemiseniorcommerce.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ulide
 */
@Tag(name = "Productos", description = "Endpoint para gestionar productos")
@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Obtiene la informacion de un producto a traves de un ID
     *
     * @param id ID del producto
     * @return Informacion del producto
     */
    @Operation(
            summary = "Obtener producto",
            description = "Devuelve un producto mediante registrado con el ID proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente."),
                    @ApiResponse(responseCode = "404", description = " no encontrado.")})
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProducto(@PathVariable Long id) {
        ProductoDto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Obtiene todos los productos y su informacion
     *
     * @return Informacion de todos los productos
     */
    @Operation(
            summary = "Obtener todos los productos",
            description = "Devuelve todos los productos y su informacion.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Se encontraron x productos.")})
    @GetMapping
    public ResponseEntity<Page<ProductoDto>> getAllProductos(
            @Valid @RequestParam(required = false) Double precioMin,
            @Valid @RequestParam(required = false) Double precioMax,
            @Valid @RequestParam(defaultValue = "0") int page,
            @Valid @RequestParam(defaultValue = "5") int size) {
        Page<ProductoDto> productos = productoService.getAllProductos(precioMin, precioMax, page, size);
        return ResponseEntity.ok(productos);
    }

    /**
     * Se crea un nuevo producto
     *
     * @param producto Son los datos del producto
     * @return Producto creado.
     */
    @Operation(
            summary = "Crear producto",
            description = "Crea un producto.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "El producto se ha creado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos.")})
    @PostMapping
    public ResponseEntity<ProductoDto> createProducto(@Valid @RequestBody ProductoRequest producto) {
        ProductoDto productoDto = productoService.createProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoDto);
    }

    /**
     * Actualiza un producto existente
     *
     * @param id       ID del producto que se quiere actualiat
     * @param producto Datos actualizados del producto
     * @return Producto actualizado
     */
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza la informacion de un producto registrado con el ID proporcionado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Argumentos invalidos."),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado.")})
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest producto) {
        ProductoDto productoDto = productoService.updateProducto(id, producto);
        return ResponseEntity.ok(productoDto);
    }

    /**
     * Busca productos en base a filtros. En caso de no especificarlos, retorna todos los productos.
     *
     * @param palabrasClave Palabras que sirven para buscar producto entre su nombre y descripcion.
     * @param precioMin     Precio minimo de producto.
     * @param precioMax     Precio maximo de producto.
     * @param page          Numero de pagina.
     * @param size          Tamanio de la muestra de la pagina.
     * @return Productos encontrados en base a los filtros.
     */
    @Operation(
            summary = "Buscar productos",
            description = "Busca productos mediante una palabra clave y precios de producto.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos encontrados.")})
    @GetMapping("/search")
    public ResponseEntity<Page<ProductoDto>> searchProductos(
            @Valid @RequestParam(required = false) String palabrasClave,
            @Valid @RequestParam(required = false) Double precioMin,
            @Valid @RequestParam(required = false) Double precioMax,
            @Valid @PositiveOrZero(message = "El numero de la pagina tiene que ser positivo.") @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @Positive(message = "El tamanio de la muestra tiene que ser mayor que cero.") @RequestParam(required = false, defaultValue = "5") int size) {
        Page<ProductoDto> productos = productoService.getProductosBySearch(
                palabrasClave, precioMin, precioMax, page, size);
        return ResponseEntity.ok(productos);
    }

    /**
     * Elimina un producto. En caso de estar referenciado (en un pedido por ejemplo), se actualizara el ID a null, el precio a 0.0 o null, y se dara un mensaje de no disponibilidad segun corresponda.
     *
     * @param id ID del producto que se quiere eliminar
     * @return Mensaje sin contenido.
     */
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto registrado con el ID proporcionado. Los pedidos que contengan productos con el ID especificado, ser actualizados: id_producto sera actualizado a null, el precio del producto sera actualizado a 0.0 o null, segun corresponda; y se mostrara un mensaje de no disponibilidad segun corresponda.",
            responses = {@ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente.")})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
