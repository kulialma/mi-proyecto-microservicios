package com.example.micro_productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InventarioNoEncontradoException extends ResponseStatusException {
    public InventarioNoEncontradoException() {
        super(HttpStatus.NOT_FOUND, "Inventario no encontrado");
    }
}


