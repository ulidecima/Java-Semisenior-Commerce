package com.ulises.javasemiseniorcommerce.repository;

import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {
    @Query("SELECT p FROM ProductoModel p " +
            "WHERE (LOWER(p.nombre) LIKE :palabrasClave OR LOWER(p.descripcion) LIKE :palabrasClave) " +
            "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
            "AND (:precioMax IS NULL OR p.precio <= :precioMax)")
    List<ProductoModel> searchProductosByPalabrasClave(
            @Param("palabrasClave") String palabrasClave,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            Pageable pageable
    );
}