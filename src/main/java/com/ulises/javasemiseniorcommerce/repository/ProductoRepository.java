package com.ulises.javasemiseniorcommerce.repository;

import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {
}