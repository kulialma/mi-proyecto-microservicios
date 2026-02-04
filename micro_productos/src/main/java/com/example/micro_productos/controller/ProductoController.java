package com.example.micro_productos.controller;

import com.example.micro_productos.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private Map<Long, Producto> productos = new HashMap<>();
    private Long contador = 1L;

    // Crear producto
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) {
        producto.setId(contador++);
        productos.put(producto.getId(), producto);

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

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProducto(@PathVariable Long id) {
        Producto producto = productos.get(id);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

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

    // Listar productos
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarProductos() {
        List<Map<String, Object>> data = new ArrayList<>();
        for (Producto producto : productos.values()) {
            data.add(Map.of(
                "type", "producto",
                "id", producto.getId(),
                "attributes", Map.of(
                    "nombre", producto.getNombre(),
                    "precio", producto.getPrecio(),
                    "descripcion", producto.getDescripcion()
                )
            ));
        }
        return ResponseEntity.ok(Map.of("data", data));
    }

    // Endpoint público
    @GetMapping("/public")
    public Map<String, Object> publico() {
        return Map.of("meta", Map.of("mensaje", "Microservicio Productos activo"));
    }
}




