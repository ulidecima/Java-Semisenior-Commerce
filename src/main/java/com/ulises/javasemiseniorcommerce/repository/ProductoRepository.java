package com.ulises.javasemiseniorcommerce.repository;

import com.ulises.javasemiseniorcommerce.model.ProductoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {
    @Query("SELECT p FROM ProductoModel p " +
            "WHERE (:palabrasClave IS NULL OR " +
            "LOWER(p.nombre) LIKE CONCAT('%', :palabrasClave, '%') OR " +
            "LOWER(p.descripcion) LIKE CONCAT('%', :palabrasClave, '%')) " +
            "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
            "AND (:precioMax IS NULL OR p.precio <= :precioMax) " +
            "ORDER BY p.precio")
    Page<ProductoModel> searchProductosByPalabrasClave(
            @Param("palabrasClave") String palabrasClave,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            Pageable pageable
    );
}