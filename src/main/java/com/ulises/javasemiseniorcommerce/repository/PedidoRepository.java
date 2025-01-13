package com.ulises.javasemiseniorcommerce.repository;

import com.ulises.javasemiseniorcommerce.model.PedidoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {
    Page<PedidoModel> findAllByUsuario_Id(Long usuarioId, Pageable pageable);
}