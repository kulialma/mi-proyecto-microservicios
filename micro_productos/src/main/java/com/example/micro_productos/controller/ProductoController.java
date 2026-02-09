package com.example.micro_productos.controller;

import com.example.micro_productos.model.Producto;
import com.example.micro_productos.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Crear producto
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoRepository.save(producto);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "producto",
                "id", nuevo.getId(),
                "attributes", Map.of(
                    "nombre", nuevo.getNombre(),
                    "descripcion", nuevo.getDescripcion(),
                    "precio", nuevo.getPrecio()
                )
            )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar productos
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
                    "descripcion", p.getDescripcion(),
                    "precio", p.getPrecio()
                )
            ));
        }

        return ResponseEntity.ok(Map.of("data", data));
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProducto(@PathVariable Long id) {
        Optional<Producto> opt = productoRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Producto no encontrado"))));
        }

        Producto p = opt.get();
        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "producto",
                "id", p.getId(),
                "attributes", Map.of(
                    "nombre", p.getNombre(),
                    "descripcion", p.getDescripcion(),
                    "precio", p.getPrecio()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Optional<Producto> opt = productoRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Producto no encontrado"))));
        }

        Producto p = opt.get();
        p.setNombre(producto.getNombre());
        p.setDescripcion(producto.getDescripcion());
        p.setPrecio(producto.getPrecio());
        Producto actualizado = productoRepository.save(p);

        Map<String, Object> response = Map.of(
            "data", Map.of(
                "type", "producto",
                "id", actualizado.getId(),
                "attributes", Map.of(
                    "nombre", actualizado.getNombre(),
                    "descripcion", actualizado.getDescripcion(),
                    "precio", actualizado.getPrecio()
                )
            )
        );

        return ResponseEntity.ok(response);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errors", List.of(Map.of("detail", "Producto no encontrado"))));
        }

        productoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("meta", Map.of("mensaje", "Producto eliminado con éxito")));
    }

    // Endpoint público
    @GetMapping("/public")
    public Map<String, Object> publico() {
        return Map.of("meta", Map.of("mensaje", "Microservicio Productos activo"));
    }
}








