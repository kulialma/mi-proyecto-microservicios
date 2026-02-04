package com.example.micro_inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inventario {

    @Id
    private Long productoId;
    private Integer cantidad;

    public Inventario() {
    }

    public Inventario(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return "Inventario{" +
                "productoId=" + productoId +
                ", cantidad=" + cantidad +
                '}';
    }
}




