package com.example.micro_productos.service;

import com.example.micro_productos.model.Inventario;
import com.example.micro_productos.repository.InventarioRepository;
import com.example.micro_productos.exception.InventarioNoEncontradoException;
import com.example.micro_productos.exception.StockInsuficienteException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public Inventario crearInventario(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public List<Inventario> listarInventario() {
        return inventarioRepository.findAll();
    }

    public Inventario obtenerInventarioPorProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(InventarioNoEncontradoException::new);
    }

    public Inventario realizarCompra(Long productoId, int cantidad) {
        Inventario inv = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(InventarioNoEncontradoException::new);

        if (inv.getCantidad() < cantidad) {
            throw new StockInsuficienteException();
        }

        inv.setCantidad(inv.getCantidad() - cantidad);
        return inventarioRepository.save(inv);
    }
}



