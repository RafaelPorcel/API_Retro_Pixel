package com.example.api_retro_pixel.controller;

import com.example.api_retro_pixel.dto.CategoriaDto;
import com.example.api_retro_pixel.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaDto> crearCategoria (@RequestBody CategoriaDto nuevaCategoria) {
        CategoriaDto categoriaCreada = categoriaService.crear(nuevaCategoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> listarCategorias () {
        List<CategoriaDto> listaCategorias = categoriaService.listarCategorias();
        return ResponseEntity.ok().body(listaCategorias);
    }
}
