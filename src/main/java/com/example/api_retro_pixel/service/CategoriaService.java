package com.example.api_retro_pixel.service;

import com.example.api_retro_pixel.dto.CategoriaDto;
import com.example.api_retro_pixel.model.Categoria;
import com.example.api_retro_pixel.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    // Listar: convertimos la lista de Categorias a CategoriasDto
    // utilizando el método creado para ello
    public List<CategoriaDto> listarCategorias () {
        return categoriaRepository.findAll()
                .stream()
                .map(this::categoriaToDto)
                .collect(Collectors.toList());
    }

    // Crear: recibimos Dto y lo guardamos como Entidad
    // en POST solo pasamos el atributo 'nombre' porque id se autogenera y la lista de artículos no queremos que pase,
    // pero devolvemos Dto para que el cliente vea qué se ha guardado: id y nombre
    public CategoriaDto crear (CategoriaDto nuevaCategoriaDto) {
        Categoria nuevaCategoria = categoriaDtoToCategoria(nuevaCategoriaDto);
        Categoria categoriaParaGuardar = categoriaRepository.save(nuevaCategoria);
        return categoriaToDto(categoriaParaGuardar);
    }


    // --- MÉTODOS DE MAPEO ---

    private CategoriaDto categoriaToDto (Categoria categoria) {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }

    private Categoria categoriaDtoToCategoria (CategoriaDto dto) {
        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNombre(dto.getNombre());
        return categoria;
    }
}
