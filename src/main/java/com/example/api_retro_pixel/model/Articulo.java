package com.example.api_retro_pixel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "articulos")
public class Articulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private double precio;
    private Integer stock;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
