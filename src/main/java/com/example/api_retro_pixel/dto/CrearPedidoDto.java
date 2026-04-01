package com.example.api_retro_pixel.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CrearPedidoDto {
    private Map<Long, Integer> lineasPedido;
}
