package com.example.api_retro_pixel.controller;

import com.example.api_retro_pixel.dto.CrearPedidoDto;
import com.example.api_retro_pixel.dto.PedidoDto;
import com.example.api_retro_pixel.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDto> registrarPedido(@RequestBody CrearPedidoDto crearPedidoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.registrarPedido(crearPedidoDto));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDto>> listarPedididos() {
        return ResponseEntity.ok().body(pedidoService.listarPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> buscarPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(pedidoService.buscarPedidoPorId(id));
    }

}
