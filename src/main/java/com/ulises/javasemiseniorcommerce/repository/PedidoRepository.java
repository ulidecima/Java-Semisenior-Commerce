package com.ulises.javasemiseniorcommerce.repository;

import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {
    List<PedidoModel> findAllByUsuario_Id(Long usuarioId);
}