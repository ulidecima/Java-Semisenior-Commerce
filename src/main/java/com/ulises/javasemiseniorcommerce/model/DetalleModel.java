package com.ulises.javasemiseniorcommerce.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.annotation.Description;

/**
 * @author ulide
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "detalles")
@Description("Esta es una tabla intermedia entre el pedido y los productos. " +
        "Ya que un pedido puede tener uno o varios productos diferentes, " +
        "se hace necesario usar esta tabla para representar la relacion " +
        "uno a muchos: un pedido tiene muchos productos.")
public class DetalleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL) // Cuando se elimina un producto, este campo se actualiza a null
                                                // para no perder informacion de los pedidos.
    @JoinColumn(name = "producto_id", nullable = true)
    private ProductoModel producto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoModel pedido;

    @Column(nullable = false)
    private Integer cantidad;
}
