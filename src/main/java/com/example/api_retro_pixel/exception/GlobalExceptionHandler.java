package com.example.api_retro_pixel.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j // Anotación mágica para habilitar la variable 'log'
@RestControllerAdvice // Convierte las respuestas automáticamente a JSON
public class GlobalExceptionHandler {

    // --- 1. ERROR: ARTÍCULO NO ENCONTRADO (Devuelve Status 404) ---
    @ExceptionHandler(ArticuloNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> manejarArticuloNoEncontrado(ArticuloNoEncontradoException ex) {

        // Guardamos el error en el archivo log que está en la raiz del proyecto
        // para nosotros creado a través de unas indicaciones en application.properties
        if (ex.getCause() != null) {
            log.error("[Not Found] Causa raíz del error ArticulonoEncontrado: {}", ex.getCause().getMessage());
        } else {
            log.error("[Not Found] {}", ex.getMessage());
        }

        // Construimos el JSON limpio para el cliente
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Not Found");
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("status", HttpStatus.NOT_FOUND.value());

        return respuesta;
    }

    // --- 2. ERROR: STOCK INSUFICIENTE (Devuelve Status 400) ---
    @ExceptionHandler(StockInsuficienteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> manejarStockInsuficiente(StockInsuficienteException ex) {

        // Guardamos el error en el archivo log creado en application.properties
        if (ex.getCause() != null) {
            log.error("[Bad Request] Causa raíz del error StockInsuficiente: {}", ex.getCause().getMessage());
        } else {
            log.error("[Bad Request] {}", ex.getMessage());
        }

        // JSON para el cliente
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Bad Request");
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());

        return respuesta;
    }
}