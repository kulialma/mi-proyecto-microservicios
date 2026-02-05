package com.example.micro_productos.controller;

import com.example.micro_productos.model.Inventario;
import com.example.micro_productos.service.InventarioService;
import com.example.micro_productos.exception.InventarioNoEncontradoException;
import com.example.micro_productos.exception.StockInsuficienteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // ===============================
    // Crear inventario
    // ===============================
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

    // ===============================
    // Listar inventario completo
    // ===============================
    @GetMapping
    public ResponseEntity<?> listarInventario() {
        List<Inventario> inventarios = inventarioService.listarInventario();
        return ResponseEntity.ok(
            Map.of("data", inventarios.stream().map(inv -> Map.of(
                "type", "inventario",
                "id", inv.getId(),
                "attributes", Map.of(
                    "productoId", inv.getProductoId(),
                    "cantidad", inv.getCantidad()
                )
            )).toList())
        );
    }

    // ===============================
    // Consultar inventario por productoId
    // ===============================
    @GetMapping("/{productoId}")
    public ResponseEntity<?> obtenerInventarioPorProducto(@PathVariable Long productoId) {
        try {
            Inventario inv = inventarioService.obtenerInventarioPorProducto(productoId);
            return ResponseEntity.ok(
                Map.of("data", Map.of(
                    "type", "inventario",
                    "id", inv.getId(),
                    "attributes", Map.of(
                        "productoId", inv.getProductoId(),
                        "cantidad", inv.getCantidad()
                    )
                ))
            );
        } catch (InventarioNoEncontradoException e) {
            return ResponseEntity.status(404).body(
                Map.of("errors", List.of(Map.of("detail", "Producto no existe en inventario")))
            );
        }
    }

    // ===============================
    // Realizar compra
    // ===============================
    @PostMapping("/compras")
    public ResponseEntity<?> realizarCompra(@RequestBody Map<String, Object> body) {
        Long productoId = Long.valueOf(body.get("productoId").toString());
        int cantidad = Integer.parseInt(body.get("cantidad").toString());

        try {
            Inventario actualizado = inventarioService.realizarCompra(productoId, cantidad);

            return ResponseEntity.status(201).body(
                Map.of("data", Map.of(
                    "type", "compra",
                    "id", actualizado.getId(),
                    "attributes", Map.of(
                        "productoId", actualizado.getProductoId(),
                        "cantidadComprada", cantidad,
                        "cantidadRestante", actualizado.getCantidad(),
                        "mensaje", "Compra realizada con éxito"
                    )
                ))
            );
        } catch (StockInsuficienteException e) {
            return ResponseEntity.badRequest().body(
                Map.of("errors", List.of(Map.of("detail", "Inventario insuficiente")))
            );
        } catch (InventarioNoEncontradoException e) {
            return ResponseEntity.status(404).body(
                Map.of("errors", List.of(Map.of("detail", "Producto no existe en inventario")))
            );
        }
    }
}



