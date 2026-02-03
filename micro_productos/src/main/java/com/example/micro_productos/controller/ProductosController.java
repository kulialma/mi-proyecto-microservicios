package com.example.micro_productos.controller;

import com.example.micro_productos.model.Producto;
import com.example.micro_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    @Autowired
    private ProductoRepository productoRepository;

    // Endpoint de verificación rápida
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("message", "Microservicio Productos activo");
    }

    // Crear un nuevo producto (requiere API Key)
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) {
        Producto saved = productoRepository.save(producto);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "producto",
                "id", saved.getId(),
                "attributes", Map.of(
                    "nombre", saved.getNombre(),
                    "precio", saved.getPrecio(),
                    "descripcion", saved.getDescripcion()
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtener un producto específico por ID (requiere API Key)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "producto",
                "id", producto.getId(),
                "attributes", Map.of(
                    "nombre", producto.getNombre(),
                    "precio", producto.getPrecio(),
                    "descripcion", producto.getDescripcion()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Listar todos los productos (requiere API Key)
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarProductos() {
        List<Producto> productos = productoRepository.findAll();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Producto p : productos) {
            data.add(Map.of(
                "type", "producto",
                "id", p.getId(),
                "attributes", Map.of(
                    "nombre", p.getNombre(),
                    "precio", p.getPrecio(),
                    "descripcion", p.getDescripcion()
                )
            ));
        }

        Map<String, Object> response = Map.of("data", data);
        return ResponseEntity.ok(response);
    }

    // 🔓 Endpoint público para ver productos en navegador (sin API Key)
    @GetMapping("/public")
    public Map<String, Object> listarProductosPublico() {
        List<Producto> productos = productoRepository.findAll();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Producto p : productos) {
            data.add(Map.of(
                "type", "producto",
                "id", p.getId(),
                "attributes", Map.of(
                    "nombre", p.getNombre(),
                    "precio", p.getPrecio(),
                    "descripcion", p.getDescripcion()
                )
            ));
        }

        return Map.of("data", data);
    }
}


