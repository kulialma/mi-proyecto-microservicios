package com.example.micro_inventario.controller;

import com.example.micro_inventario.model.Inventario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private Map<Long, Inventario> inventarios = new HashMap<>();

    // Crear inventario inicial
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearInventario(@RequestBody Inventario inventario) {
        if (inventarios.containsKey(inventario.getProductoId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario ya existe"))));
        }
        inventarios.put(inventario.getProductoId(), inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", inventario.getProductoId(),
                "attributes", Map.of(
                    "cantidad", inventario.getCantidad()
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Consultar inventario por productoId
    @GetMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> consultarInventario(@PathVariable Long productoId) {
        Inventario inventario = inventarios.get(productoId);
        if (inventario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario no encontrado"))));
        }

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", inventario.getProductoId(),
                "attributes", Map.of(
                    "cantidad", inventario.getCantidad()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Actualizar inventario
    @PutMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizarInventario(@PathVariable Long productoId,
                                                                    @RequestBody Inventario inventario) {
        inventario.setProductoId(productoId);
        inventarios.put(productoId, inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", inventario.getProductoId(),
                "attributes", Map.of(
                    "cantidad", inventario.getCantidad()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint de compra
    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>> realizarCompra(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());

        Inventario inventario = inventarios.get(productoId);

        if (inventario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Producto no existe en inventario"))));
        }

        if (inventario.getCantidad() < cantidad) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario insuficiente"))));
        }

        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventarios.put(productoId, inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "compra",
                "id", UUID.randomUUID().toString(),
                "attributes", Map.of(
                    "productoId", productoId,
                    "cantidadComprada", cantidad,
                    "cantidadRestante", inventario.getCantidad(),
                    "mensaje", "Compra realizada con éxito"
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint de salud
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("meta", Map.of("mensaje", "Microservicio Inventario activo"));
    }
}







