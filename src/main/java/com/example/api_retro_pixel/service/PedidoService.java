package com.example.api_retro_pixel.service;

import com.example.api_retro_pixel.dto.CrearPedidoDto;
import com.example.api_retro_pixel.dto.PedidoDto;
import com.example.api_retro_pixel.model.Articulo;
import com.example.api_retro_pixel.model.Pedido;
import com.example.api_retro_pixel.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ArticuloService articuloService;

    public PedidoDto registrarPedido(CrearPedidoDto crearPedidoDto) {
        // Creo una lista de Articulos para hacer el pedido.
        List<Articulo> articulosParaPedido = new ArrayList<>();

        // Recorremos el Map que nos llega por atributo de la clase CrearPedidoDto
        for (Map.Entry<Long, Integer> entry : crearPedidoDto.getLineasPedido().entrySet()) {
            // Obtenemos los Long del Map (la key), que son los Id de cada Artículo del Map
            Long idArticulo = entry.getKey();
            // Y obtenemos los Integer (los values, que son la cantidad de Articulos con ese Id que compra el cliente
            Integer cantidadComprada = entry.getValue();

            /*
            -Actualizamos el stock de cada Articulo comprado por el cliente mediante nuestro método que nos pedía
            -El Id y la cantidad que los hemos obtenido recorriendo el Map*/
            /*
            -Esta línea también crea un objeto Artículo que con su Id y cantidad que es el que pasamos al for de abajo
            para que lo añada a la lista de Articulos para pedido creada al principio
            -Vamos rellenando la lista de Artículos que luego vamos a usar para operar con ella más adelante
            tanto para calcular la cantidad de consolas o videojuegos que compra el cliente y para hacer el total del precio
            incluso para agregar en el objeto Pedido para guardarlo en la base de datos y que luego se mapea a PedidoDto*/
            Articulo articuloActualizado = articuloService.actualizarStock(idArticulo, cantidadComprada);
            // Se añade a la lista de articuloParaPedido el articuloActualizado tantas veces como cantidadComprada tenga
            for (int i = 0; i < cantidadComprada; i++) {
                articulosParaPedido.add(articuloActualizado);
            }
        }

        // Así consigo ver cuantos Articulos tengo de cada tipo 'consolas o videojuegos' para luego utilizarlo en las ofertas
        long cantidadConsolas = articulosParaPedido.stream()
                .filter(articulo -> articulo.getCategoria().getNombre().equalsIgnoreCase("consolas"))
                .count();
        long cantidadVideojuegos = articulosParaPedido.stream()
                .filter(articulo -> articulo.getCategoria().getNombre().equalsIgnoreCase("videojuegos"))
                .count();

        // Así hago la suma del precio de todos los Articulos de la lista articulosParaPedido
        double precioTotal = articulosParaPedido.stream()
                .mapToDouble(Articulo::getPrecio)
                .sum();

        // Estas son las reglas de negocio que pide el cliente
        // Si un cliente compra más de 3 videojuegos en el mismo pedido, le hacemos un 15% de descuento en toda su compra
        if (cantidadVideojuegos > 3) {
            precioTotal *= 0.85;
        }
        // Si el cliente se lleva al menos 1 consola, le descontamos 20 € fijos del total del ticket
        if (cantidadConsolas >= 1) {
            precioTotal -= 20;
            if (precioTotal < 0) {
                precioTotal = 0.0;
            }
        }
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setFecha(LocalDateTime.now());
        nuevoPedido.setArticulosComprados(articulosParaPedido);
        nuevoPedido.setTotal(precioTotal);

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        return pedidoToDto(pedidoGuardado);
    }

    public PedidoDto buscarPedidoPorId (Long id) {
        return pedidoToDto(pedidoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("El pedido con ID: " + id +" no existe"))
        );
    }

    public List<PedidoDto> listarPedidos () {
        List<Pedido> listaPedido = pedidoRepository.findAll();

        return listaPedido.stream()
                .map(pedido -> pedidoToDto(pedido))
                .collect(Collectors.toList());
    }


    // --- MÉTODOS DE MAPEO ---

    private PedidoDto pedidoToDto(Pedido nuevoPedido) {
        PedidoDto dto = new PedidoDto();
        dto.setId(nuevoPedido.getId());
        dto.setFecha(nuevoPedido.getFecha());
        dto.setArticulosComprados(
                nuevoPedido.getArticulosComprados()
                        .stream()
                        .map(articulo -> articuloService.articuloToDto(articulo))
                        .collect(Collectors.toList())
        );
        dto.setTotal(nuevoPedido.getTotal());
        return dto;
    }


}
