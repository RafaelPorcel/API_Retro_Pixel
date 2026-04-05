package com.example.api_retro_pixel.service;

import com.example.api_retro_pixel.dto.ArticuloDto;
import com.example.api_retro_pixel.dto.CrearArticuloDto;
import com.example.api_retro_pixel.exception.ArticuloNoEncontradoException;
import com.example.api_retro_pixel.exception.StockInsuficienteException;
import com.example.api_retro_pixel.model.Articulo;
import com.example.api_retro_pixel.model.Categoria;
import com.example.api_retro_pixel.repository.ArticuloRepository;
import com.example.api_retro_pixel.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Queremos que devuelva un Dto para no mostrar el stock
    public List<ArticuloDto> listarArticulos() {
        return articuloRepository.findAll()
                .stream()
                .map(this::articuloToDto)
                .collect(Collectors.toList());
    }

    public Articulo buscarArticuloPorId(Long id) {
        return articuloRepository.findById(id).orElseThrow(
                () -> new ArticuloNoEncontradoException("El artículo con ID " + id + " no existe")
        );
    }

    // Transforma el ID de categoría del DTO en una entidad Categoria real buscando en BD.
    // Construye la entidad Articulo que sí incluye el stock inicial (dato privado).
    // Guarda en MySQL y retorna el "escaparate seguro" (ArticuloDto) ocultando el stock.
    public ArticuloDto crearArticulo(CrearArticuloDto crearArticuloDto) {
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

    public Articulo actualizarStock(Long id, Integer cantidadComprada) {
        Articulo articuloParaActualizarStock = buscarArticuloPorId(id);
        Integer stock = articuloParaActualizarStock.getStock();
        if (stock < cantidadComprada) {
            throw new StockInsuficienteException(
                    "No hay stock suficiente para el artículo con id: " + id + " y nombre: " + articuloParaActualizarStock.getTitulo()
            );
        }
        Integer nuevoStock =  stock - cantidadComprada;
        articuloParaActualizarStock.setStock(nuevoStock);
        return articuloRepository.save(articuloParaActualizarStock);
    }


    // --- MÉTODOS DE MAPEO ---

    private ArticuloDto articuloToDto(Articulo articulo) {
        ArticuloDto dto = new ArticuloDto();
        dto.setId(articulo.getId());
        dto.setTitulo(articulo.getTitulo());
        dto.setPrecio(articulo.getPrecio());
        dto.setCategoriaId(articulo.getCategoria().getId());
        return dto;
    }

}
