package com.ulises.javasemiseniorcommerce.testUtils;

import com.ulises.javasemiseniorcommerce.dto.*;
import com.ulises.javasemiseniorcommerce.model.DetalleModel;
import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import com.ulises.javasemiseniorcommerce.model.UsuarioModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ulide
 */
@Data
public class TestDataFactory {
    public static AuthRequest crearAuthRequest() {
        return AuthRequest.builder()
                .email("test@mail.com")
                .password("psswrd")
                .build();
    }

    public static RegisterRequest crearRegisterRequest() {
        return RegisterRequest.builder()
                .nombre("Usuario Test")
                .email("test@mail.com")
                .password("psswrd")
                .build();
    }

    public static UsuarioModel crearUsuarioModel() {
        return UsuarioModel.builder()
                .id(1L)
                .nombre("Usuario Test")
                .email("test@mail.com")
                .password("psswrd")
                .habilitado(true)
                .build();
    }

    public static UsuarioRequest crearUsuarioRequest() {
        return UsuarioRequest.builder()
                .nombre("Usuario Test")
                .email("test@mail.com")
                .password("psswrd")
                .habilitado(true)
                .build();
    }

    public static ProductoModel crearProductoModel() {
        return ProductoModel.builder()
                .id(1L)
                .nombre("Producto Test")
                .descripcion("Descripcion Test")
                .precio(100.0)
                .stockDisponible(100)
                .build();
    }

    public static ProductoRequest crearProductoRequest() {
        return ProductoRequest.builder()
                .nombre("Producto Test")
                .descripcion("Descripcion Test")
                .precio(100.0)
                .stockDisponible(100)
                .build();
    }

    public static PedidoRequest crearPedidoRequest() {
        return PedidoRequest.builder()
                .username("test@mail.com")
                .detalles(List.of(
                        DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(2)
                                .build()
                )).build();
    }

    public static PedidoModel crearPedido(UsuarioModel usuario, List<DetalleModel> detalles) {
        return PedidoModel.builder()
                .id(1L)
                .usuario(usuario)
                .detalles(detalles)
                .precio(detalles.stream()
                        .mapToDouble(detalle ->
                                detalle.getProducto().getPrecio() * detalle.getCantidad())
                        .sum())
                .fechaDeCreacion(LocalDateTime.now())
                .build();
    }

    public static PedidoModel crearPedido() {
        return PedidoModel.builder()
                .id(1L)
                .usuario(crearUsuarioModel())
                .detalles(List.of())
                .precio(0.0)
                .fechaDeCreacion(LocalDateTime.now())
                .build();
    }
}
