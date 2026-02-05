package com.example.micro_inventario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InventarioNoEncontradoException extends ResponseStatusException {
    public InventarioNoEncontradoException() {
        super(HttpStatus.NOT_FOUND, "Producto no existe en inventario");
    }
}


