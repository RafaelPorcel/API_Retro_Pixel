package com.example.api_retro_pixel.repository;

import com.example.api_retro_pixel.model.Categoria;
import com.example.api_retro_pixel.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
