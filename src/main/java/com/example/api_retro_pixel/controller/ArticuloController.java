package com.example.api_retro_pixel.controller;

import com.example.api_retro_pixel.dto.ArticuloDto;
import com.example.api_retro_pixel.dto.CrearArticuloDto;
import com.example.api_retro_pixel.model.Articulo;
import com.example.api_retro_pixel.service.ArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {
    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public ResponseEntity<List<ArticuloDto>> listarArticulos () {
        return ResponseEntity.ok().body(articuloService.listarArticulos());
    }

    @PostMapping
    public ResponseEntity<ArticuloDto> crearArticulo (@RequestBody CrearArticuloDto nuevoArticulo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloService.crearArticulo(nuevoArticulo));
    }


}
