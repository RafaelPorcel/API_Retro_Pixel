package com.example.api_retro_pixel.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoDto {
    private Long id;
    private LocalDateTime fecha;
    private List<ArticuloDto> articulosComprados;
    private Double total;
}
