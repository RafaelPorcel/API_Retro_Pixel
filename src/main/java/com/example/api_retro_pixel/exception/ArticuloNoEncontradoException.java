package com.example.api_retro_pixel.exception;

public class ArticuloNoEncontradoException extends RuntimeException {
    public ArticuloNoEncontradoException(String message) {
        super(message);
    }

    public ArticuloNoEncontradoException(String message, Throwable cause) { super(message, cause); }
}
