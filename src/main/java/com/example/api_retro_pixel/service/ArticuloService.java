package com.example.api_retro_pixel.service;

import com.example.api_retro_pixel.dto.ArticuloDto;
import com.example.api_retro_pixel.dto.CrearArticuloDto;
import com.example.api_retro_pixel.model.Articulo;
import com.example.api_retro_pixel.model.Categoria;
import com.example.api_retro_pixel.repository.ArticuloRepository;
import com.example.api_retro_pixel.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    // Necesario para buscar la categoria a partir del Id
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<ArticuloDto> listarArticulos () {
        return articuloRepository.findAll()
                .stream()
                .map(this::articuloToDto)
                .collect(Collectors.toList());
    }

    public Optional<Articulo> buscarArticuloPorId (Long id) {
        return articuloRepository.findById(id);
    }

    public ArticuloDto crearArticulo (CrearArticuloDto crearArticuloDto) {
        Categoria categoriaIdDelArticulo = categoriaRepository.findById(crearArticuloDto.getCategoriaId()).orElse(null);
        Articulo nuevoArticulo = new Articulo();
        nuevoArticulo.setTitulo(crearArticuloDto.getTitulo());
        nuevoArticulo.setPrecio(crearArticuloDto.getPrecio());
        nuevoArticulo.setCategoria(categoriaIdDelArticulo);
        nuevoArticulo.setStock(crearArticuloDto.getStock());
        Articulo articuloParaGuardar = articuloRepository.save(nuevoArticulo);
        return articuloToDto(articuloParaGuardar);
        // Asi se haría en una línea todo ->
        //return articuloToDto(articuloRepository.save(nuevoArticulo));
    }

    // --- MÉTODOS DE MAPEO ---

    private ArticuloDto articuloToDto (Articulo articulo) {
        ArticuloDto dto = new ArticuloDto();
        dto.setId(articulo.getId());
        dto.setTitulo(articulo.getTitulo());
        dto.setPrecio(articulo.getPrecio());
        dto.setCategoriaId(articulo.getCategoria().getId());
        return dto;
    }

}
