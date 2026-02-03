package com.example.micro_productos.controller;

import com.example.micro_productos.model.Inventario;
import com.example.micro_productos.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping
    public ResponseEntity<?> crearInventario(@RequestBody Inventario inventario) {
        Inventario nuevo = inventarioService.crearInventario(inventario);
        return ResponseEntity.status(201).body(
            Map.of("data", Map.of(
                "type", "inventario",
                "id", nuevo.getId(),
                "attributes", Map.of(
                    "productoId", nuevo.getProductoId(),
                    "cantidad", nuevo.getCantidad()
                )
            ))
        );
    }

    @PostMapping("/compra")
    public ResponseEntity<?> realizarCompra(@RequestBody Map<String, Object> body) {
        Long productoId = Long.valueOf(body.get("productoId").toString());
        int cantidad = Integer.parseInt(body.get("cantidad").toString());

        Inventario actualizado = inventarioService.realizarCompra(productoId, cantidad);

        return ResponseEntity.ok(
            Map.of("data", Map.of(
                "type", "compra",
                "id", actualizado.getId(),
                "attributes", Map.of(
                    "productoId", actualizado.getProductoId(),
                    "cantidad", cantidad
                )
            ))
        );
    }
}

