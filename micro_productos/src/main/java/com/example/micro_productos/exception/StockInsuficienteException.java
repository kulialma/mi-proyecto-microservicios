package com.example.micro_productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StockInsuficienteException extends ResponseStatusException {
    public StockInsuficienteException() {
        super(HttpStatus.BAD_REQUEST, "Stock insuficiente");
    }
}
