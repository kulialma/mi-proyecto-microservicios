package com.example.micro_inventario.controller;

import com.example.micro_inventario.model.Inventario;
import com.example.micro_inventario.repository.InventarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioRepository inventarioRepository;

    public InventarioController(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    // ===============================
    // Crear inventario inicial
    // ===============================
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearInventario(@RequestBody Inventario inventario) {
        if (inventarioRepository.existsById(inventario.getProductoId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario ya existe"))));
        }
        Inventario nuevo = inventarioRepository.save(inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", nuevo.getProductoId(),
                "attributes", Map.of("cantidad", nuevo.getCantidad())
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================
    // Listar todos los inventarios
    // ===============================
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarInventario() {
        List<Inventario> inventarios = inventarioRepository.findAll();

        List<Map<String, Object>> data = inventarios.stream().map(inv -> Map.of(
            "type", "inventario",
            "id", inv.getProductoId(),
            "attributes", Map.of("cantidad", inv.getCantidad())
        )).toList();

        return ResponseEntity.ok(Map.of("data", data));
    }

    // ===============================
    // Consultar inventario por productoId
    // ===============================
    @GetMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> consultarInventario(@PathVariable Long productoId) {
        Optional<Inventario> opt = inventarioRepository.findByProductoId(productoId);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario no encontrado"))));
        }

        Inventario inv = opt.get();
        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", inv.getProductoId(),
                "attributes", Map.of("cantidad", inv.getCantidad())
            )
        );

        return ResponseEntity.ok(response);
    }

    // ===============================
    // Actualizar inventario
    // ===============================
    @PutMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizarInventario(@PathVariable Long productoId,
                                                                    @RequestBody Inventario inventario) {
        inventario.setProductoId(productoId);
        Inventario actualizado = inventarioRepository.save(inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", actualizado.getProductoId(),
                "attributes", Map.of("cantidad", actualizado.getCantidad())
            )
        );

        return ResponseEntity.ok(response);
    }

    // ===============================
    // Endpoint de compra
    // ===============================
    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>> realizarCompra(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());

        Optional<Inventario> opt = inventarioRepository.findByProductoId(productoId);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Producto no existe en inventario"))));
        }

        Inventario inv = opt.get();
        if (inv.getCantidad() < cantidad) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", List.of(Map.of("detail", "Inventario insuficiente"))));
        }

        inv.setCantidad(inv.getCantidad() - cantidad);
        inventarioRepository.save(inv);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "compra",
                "id", UUID.randomUUID().toString(),
                "attributes", Map.of(
                    "productoId", productoId,
                    "cantidadComprada", cantidad,
                    "cantidadRestante", inv.getCantidad(),
                    "mensaje", "Compra realizada con éxito"
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================
    // Endpoint de salud
    // ===============================
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("meta", Map.of("mensaje", "Microservicio Inventario activo"));
    }
}

