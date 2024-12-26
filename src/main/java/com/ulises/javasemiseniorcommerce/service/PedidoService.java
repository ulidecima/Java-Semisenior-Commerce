package com.ulises.javasemiseniorcommerce.service;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.exception.*;
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

    @Transactional
    public PedidoDto createPedido(PedidoRequest pedidoRequest) {
        logger.info("Creando pedido para el usuario: {}", pedidoRequest.getUsername());

        UsuarioModel usuario = usuarioRepository.findByEmail(pedidoRequest.getUsername())
                .orElseThrow(() -> {
                            logger.warn("Usuario no encontrado con email: {}", pedidoRequest.getUsername());
                            return new UserNotFoundException("Usuario no encontrado con email: " + pedidoRequest.getUsername());
                        });

        if (pedidoRequest.getDetalles().isEmpty()) {
            logger.warn("EL pedido no puede ser de cero productos");
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
                                return new ProductoNotFoundException("No se ha encontrado el producto");
                            });

                    if (producto.getStockDisponible() < detalle.getCantidad()) {
                        logger.warn("Stock insuficiente para el producto: {}. El stock disponible es {}.",
                                producto.getNombre(),
                                producto.getStockDisponible());
                        throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
                    }

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

    @Transactional
    public List<DetalleDto> listProductos(Long id) {
        logger.info("Listando productos del pedido con ID: {}", id);
        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    return new PedidoNotFoundException("Pedido no encontrado con ID: " + id);
                });

        List<DetalleDto> detalles = pedido.getDetalles().stream()
                .map(detalle -> DetalleDto.builder()
                        .id(detalle.getId())
                        .productoId(detalle.getProducto().getId())
                        .cantidad(detalle.getCantidad())
                        .precio(detalle.getProducto().getPrecio())
                        .build()
                ).toList();

        logger.info("Se encontraron {} productos en el pedido con ID: {}", detalles.size(), id);
        return detalles;
    }

    @Transactional
    public DetallePedidoResponse getDetalleDePedido(Long id) {
        logger.info("Obteniendo detalles del pedido con ID: {}", id);

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    return new PedidoNotFoundException("Pedido no encotrado con ID: " + id);
                });

        List<ProductoCantidad> productos = pedido.getDetalles().stream()
                .map(detalle -> ProductoCantidad.builder()
                        .nombreProducto(detalle.getProducto().getNombre())
                        .cantidad(detalle.getCantidad())
                        .build())
                .toList();

        double precioTotal = pedido.getDetalles().stream().mapToDouble(producto ->
                producto.getCantidad() * producto.getProducto().getPrecio()
        ).sum();

        return DetallePedidoResponse.builder()
                .username(pedido.getUsuario().getUsername())
                .productos(productos)
                .precioTotal(precioTotal)
                .fechaDeCreacion(LocalDateTime.now())
                .build();
    }

    public void deletePedido(Long id) {
        logger.info("Eliminando pedido con ID: {}", id);

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    return new PedidoNotFoundException("No se ha encontrado el pedido con ID: " + id);
                });

        pedidoRepository.delete(pedido);
        logger.info("Pedido eliminado correctamente con ID: {}", id);
    }

    @Transactional
    public List<PedidoDto> getPedidosByMail(String email) {
        logger.info("Buscando pedidos para el usuario con email: {}", email);

        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con email: {}", email);
                    return new UserNotFoundException("Usuario no encontrado con email: " + email);
                });

        List<PedidoDto> pedidos = pedidoRepository.findAllByUsuario_Id(usuario.getId()).stream()
                .map(this::mapToDto)
                .toList();

        logger.info("Se encontraron {} pedidos para el usuario con email: {}", pedidos.size(), email);
        return pedidos;
    }

    private PedidoDto mapToDto(PedidoModel pedido) {
        List<DetalleDto> detalles = pedido.getDetalles().stream()
                .map(detalle -> DetalleDto.builder()
                        .id(detalle.getId())
                        .productoId(detalle.getProducto().getId())
                        .cantidad(detalle.getCantidad())
                        .precio(detalle.getCantidad() * detalle.getProducto().getPrecio())
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
