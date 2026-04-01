package com.example.api_retro_pixel.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fecha;
    private Double total;
    @ManyToMany
    @JoinTable(name = "pedido_articulo")
    private List<Articulo> articulosComprados;
}
