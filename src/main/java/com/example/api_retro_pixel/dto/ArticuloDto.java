package com.example.api_retro_pixel.dto;

import lombok.Data;

@Data
public class ArticuloDto {
    private Long id;
    private String titulo;
    private double precio;
    private Long categoriaId;//Pedimos el id de la categoria
}
