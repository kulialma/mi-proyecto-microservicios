package com.example.micro_inventario.controller;

import com.example.micro_inventario.model.Inventario;
import com.example.micro_inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

   @Autowired
private RestTemplate restTemplate;


    // Endpoint de verificación rápida
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("message", "Microservicio Inventario activo");
    }

    // Crear inventario
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearInventario(@RequestBody Inventario inventario) {
        Inventario saved = inventarioRepository.save(inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", saved.getId(),
                "attributes", Map.of(
                    "productoId", saved.getProductoId(),
                    "cantidad", saved.getCantidad()
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar inventario completo
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarInventario() {
        List<Inventario> inventarios = inventarioRepository.findAll();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Inventario inv : inventarios) {
            data.add(Map.of(
                "type", "inventario",
                "id", inv.getId(),
                "attributes", Map.of(
                    "productoId", inv.getProductoId(),
                    "cantidad", inv.getCantidad()
                )
            ));
        }

        Map<String, Object> response = Map.of("data", data);
        return ResponseEntity.ok(response);
    }

    // Consultar cantidad disponible de un producto específico
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Map<String, Object>> consultarCantidad(@PathVariable Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        // Obtener información del producto desde microservicio Productos con API Key
        String url = "http://localhost:8080/productos/" + productoId;
        Map producto;
        try {
            producto = restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Timeout al consultar producto");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe");
        }

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", inventario.getId(),
                "attributes", Map.of(
                    "producto", producto.get("data"),
                    "cantidadDisponible", inventario.getCantidad()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Actualizar cantidad disponible de un producto
    @PutMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizarCantidad(@PathVariable Long productoId,
                                                                  @RequestParam Integer cantidad) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        inventario.setCantidad(cantidad);
        Inventario updated = inventarioRepository.save(inventario);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "inventario",
                "id", updated.getId(),
                "attributes", Map.of(
                    "productoId", updated.getProductoId(),
                    "cantidad", updated.getCantidad()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Registrar compra
    @PostMapping("/compra")
    public ResponseEntity<Map<String, Object>> registrarCompra(@RequestParam Long productoId,
                                                               @RequestParam Integer cantidad) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        if (inventario.getCantidad() < cantidad) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventario insuficiente");
        }

        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventarioRepository.save(inventario);

        // Obtener información del producto desde microservicio Productos con API Key
        String url = "http://localhost:8080/productos/" + productoId;
        Map producto;
        try {
            producto = restTemplate.getForObject(url, Map.class);
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Timeout al consultar producto");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe");
        }

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "compra",
                "attributes", Map.of(
                    "producto", producto.get("data"),
                    "cantidadComprada", cantidad,
                    "cantidadRestante", inventario.getCantidad()
                )
            )
        );

        return ResponseEntity.ok(response);
    }
}




