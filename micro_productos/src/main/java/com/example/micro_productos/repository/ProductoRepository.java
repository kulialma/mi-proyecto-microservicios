package com.example.micro_productos.repository;

import com.example.micro_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar producto por nombre exacto
    Optional<Producto> findByNombre(String nombre);

    // Buscar productos cuyo nombre contenga una palabra
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(Double min, Double max);
}
