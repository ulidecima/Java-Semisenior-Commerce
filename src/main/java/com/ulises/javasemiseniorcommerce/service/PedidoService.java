package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.exception.PedidoSinProductosException;
import com.ulises.javasemiseniorcommerce.exception.StockInsuficienteException;
import com.ulises.javasemiseniorcommerce.exception.PedidoNotFoundException;
import com.ulises.javasemiseniorcommerce.exception.ProductoNotFoundException;
import com.ulises.javasemiseniorcommerce.exception.UserNotFoundException;
import com.ulises.javasemiseniorcommerce.model.DetalleModel;
import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import com.ulises.javasemiseniorcommerce.repository.PedidoRepository;
import com.ulises.javasemiseniorcommerce.repository.ProductoRepository;
import com.ulises.javasemiseniorcommerce.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ulide
 */
@Service
@AllArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    /**
     * Crea un pedido.
     *
     * @param pedidoRequest Datos del pedido que se desea realizar (email del usuario, productos, cantidad)
     * @return PedidoDto con los datos del pedido.
     */
    @Transactional
    public PedidoDto createPedido(PedidoRequest pedidoRequest) {
        logger.info("Creando pedido para el usuario: {}", pedidoRequest.getUsername());

        UsuarioModel usuario = usuarioRepository.findByEmail(pedidoRequest.getUsername())
                .orElseThrow(() -> {
                            logger.warn("Usuario no encontrado con email: {}", pedidoRequest.getUsername());
                            // Excepcion por si el usuario no existe
                            return new UserNotFoundException("Usuario no encontrado con email: " + pedidoRequest.getUsername());
                        });

        if (pedidoRequest.getDetalles().isEmpty()) {
            logger.warn("EL pedido no puede ser de cero productos");
            // Excepcion por si no se agregan productos al pedido
            throw new PedidoSinProductosException("El pedido debe contener al menos un producto.");
        }

        PedidoModel pedidoModel = PedidoModel.builder()
                .usuario(usuario)
                .fechaDeCreacion(LocalDateTime.now())
                .precio(0.0)
                .build();
        pedidoRepository.save(pedidoModel);

        List<DetalleModel> detalles = pedidoRequest.getDetalles().stream()
                .map(detalle -> {
                    ProductoModel producto = productoRepository.findById(detalle.getProductoId())
                            .orElseThrow(() -> {
                                logger.warn("Producto no encontrado con ID: {}", detalle.getProductoId());
                                // Excepcion por si el producto no existe
                                return new ProductoNotFoundException("No se ha encontrado el producto");
                            });

                    if (producto.getStockDisponible() < detalle.getCantidad()) {
                        logger.warn("Stock insuficiente para el producto: {}. El stock disponible es {}.",
                                producto.getNombre(),
                                producto.getStockDisponible());
                        // Excepcion por si el stock del producto es insuficiente
                        throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
                    }

                    // Se actualiza el valor del stock
                    producto.setStockDisponible(producto.getStockDisponible() - detalle.getCantidad());
                    productoRepository.save(producto);

                    return DetalleModel.builder()
                            .producto(producto)
                            .cantidad(detalle.getCantidad())
                            .pedido(pedidoModel)
                            .build();

                }).collect(Collectors.toList());

        Double precioTotal = detalles.stream()
                .mapToDouble(detalle ->
                        detalle.getProducto().getPrecio() * detalle.getCantidad())
                .sum();

        pedidoModel.setDetalles(detalles);
        pedidoModel.setPrecio(precioTotal);
        pedidoRepository.save(pedidoModel);

        logger.info("Pedido creado exitosamente con ID: {}", pedidoModel.getId());
        return mapToDto(pedidoModel);
    }

    /**
     * Obtiene los datos de un pedido en base a un ID.
     *
     * @param id ID del pedido buscado.
     * @return PedidoDto con los datos del pedido.
     */
    @Transactional
    public PedidoDto getPedidoById(Long id) {
        logger.info("Buscando pedido con ID: {}", id);

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    return new PedidoNotFoundException("Pedido no encontrado con ID: " + id);
                });

        logger.info("Pedido encontrado con ID: {}", id);
        return mapToDto(pedido);

    }

    /**
     * Obtiene la lista de productos que hay en un pedido en base a la ID del pedido.
     *
     * @param id ID del pedido.
     * @return List<ProductoCantidad> Lista con los productos y cantidades del pedido.
     */
    @Transactional
    public List<ProductoCantidad> listProductos(Long id) {
        logger.info("Listando productos del pedido con ID: {}", id);
        PedidoModel pedido = pedidoRepository.findById(id)
                // Excepcion por si el pedido no existe
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con ID: " + id));

        List<ProductoCantidad> productos = pedido.getDetalles().stream()
                .map(detalle -> ProductoCantidad.builder()
                        .nombreProducto(detalle.getProducto() != null ?
                                // En caso de que se haya eliminado el producto se escribe este mensaje de no disponibilidad
                                detalle.getProducto().getNombre() : "Producto no disponible")
                        .cantidad(detalle.getCantidad())
                        .precioUnidad(detalle.getProducto() != null ?
                                // En caso de que se haya eliminado el producto se pone el valor del precio en 0
                                detalle.getProducto().getPrecio() : 0.0)
                        .build()
                ).toList();

        logger.info("Se encontraron {} productos en el pedido con ID: {}", productos.size(), id);
        return productos;
    }

    /**
     * Obtiene los detalles (productos, precio total y por unidad, usuario, fecha de creacion) de un pedido en base a la ID del pedido.
     * @param id ID del pedido.
     * @return DetallePedidoResponse Datos del detalle del pedido buscado (productos, precio total y por unidad, usuario, fecha de creacion).
     */
    @Transactional
    public DetallePedidoResponse getDetalleDePedido(Long id) {
        logger.info("Obteniendo detalles del pedido con ID: {}", id);

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    // Excepcion por si el pedido no existe
                    return new PedidoNotFoundException("Pedido no encotrado con ID: " + id);
                });

        List<ProductoCantidad> productos = pedido.getDetalles().stream()
                .map(detalle -> ProductoCantidad.builder()
                        .nombreProducto(detalle.getProducto() != null ?
                                // En caso de que se haya eliminado el producto se escribe este mensaje de no disponibilidad
                                detalle.getProducto().getNombre() : "Producto no disponible")
                        .cantidad(detalle.getCantidad())
                        .precioUnidad(detalle.getProducto() != null ?
                                // En caso de que se haya eliminado el producto se pone el valor del precio en 0
                                detalle.getProducto().getPrecio() : 0.0)
                        .build())
                .toList();

        return DetallePedidoResponse.builder()
                .username(pedido.getUsuario().getUsername())
                .productos(productos)
                .precioTotal(pedido.getPrecio())
                .fechaDeCreacion(LocalDateTime.now())
                .build();
    }

    /**
     * Elimina un pedido en base a un ID.
     * @param id ID del pedido que se quiere eliminar.
     */
    @Transactional
    public void deletePedido(Long id) {
        logger.info("Eliminando pedido con ID: {}", id);

        pedidoRepository.findById(id).ifPresent(pedidoRepository::delete);
        logger.info("Pedido eliminado correctamente con ID: {}", id);
    }

    /**
     * Obtiene todos los pedidos de un usuario en base al correo electronico del usuario.
     * @param email Correo electronico del usuario.
     * @param page Numero de pagina.
     * @param size Tamanio de la muestra.
     * @return Page<PedidoDto> Pagina con los datos obtenidos.
     */
    @Transactional
    public Page<PedidoDto> getPedidosByMail(String email, int page, int size) {
        logger.info("Buscando pedidos para el usuario con email: {}", email);

        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                // Excepcion por si el usuario no existe
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));

        Pageable pageable = PageRequest.of(page, size);

        Page<PedidoModel> pedidos = pedidoRepository.findAllByUsuario_Id(usuario.getId(), pageable);

        return pedidos.map(this::mapToDto);
    }

    // Metodo para mapear un PedidoModel hacia un PedidoDto
    private PedidoDto mapToDto(PedidoModel pedido) {
        List<DetalleDto> detalles = pedido.getDetalles().stream()
                .map(detalle -> DetalleDto.builder()
                        .id(detalle.getId())
                        .productoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null)
                        .cantidad(detalle.getCantidad())
                        .precio(detalle.getProducto() != null ?
                                detalle.getCantidad() * detalle.getProducto().getPrecio() : null)
                        .build()
                ).toList();

        return PedidoDto.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuario().getId())
                .username(pedido.getUsuario().getUsername())
                .detalles(detalles)
                .precio(pedido.getPrecio())
                .fechaDeCreacion(pedido.getFechaDeCreacion())
                .build();
    }
}
