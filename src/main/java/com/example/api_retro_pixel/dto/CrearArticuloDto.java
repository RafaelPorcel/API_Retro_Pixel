package com.example.api_retro_pixel.dto;

import lombok.Data;

// Este Dto sí pide introducir Stock para cuando usemos el Postman
@Data
public class CrearArticuloDto {
    private String titulo;
    private double precio;
    private Integer stock;
    private Long categoriaId;
}
