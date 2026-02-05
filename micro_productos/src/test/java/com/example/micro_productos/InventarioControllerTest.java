package com.example.micro_productos;

import com.example.micro_productos.model.Inventario;
import com.example.micro_productos.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioRepository inventarioRepository;

    @BeforeEach
    void limpiarInventario() {
        inventarioRepository.deleteAll();
    }

    @Test
    void crearInventario_deberiaRetornar201() throws Exception {
        String inventarioJson = """
            { "productoId": 1, "cantidad": 5 }
        """;

        mockMvc.perform(post("/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.productoId").value(1))
                .andExpect(jsonPath("$.data.attributes.cantidad").value(5));
    }

    @Test
    void realizarCompraExitosa_deberiaRetornar201() throws Exception {
        // Crear inventario con 10 unidades
        Inventario inv = new Inventario();
        inv.setProductoId(1L);
        inv.setCantidad(10);
        inventarioRepository.save(inv);

        String compraJson = """
            { "productoId": 1, "cantidad": 3 }
        """;

        mockMvc.perform(post("/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.cantidadRestante").value(7));
    }

    @Test
    void realizarCompra_conStockInsuficiente_deberiaRetornar400() throws Exception {
        // Crear inventario con 2 unidades
        Inventario inv = new Inventario();
        inv.setProductoId(1L);
        inv.setCantidad(2);
        inventarioRepository.save(inv);

        String compraJson = """
            { "productoId": 1, "cantidad": 5 }
        """;

        mockMvc.perform(post("/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].detail").value("Inventario insuficiente"));
    }

    @Test
    void realizarCompra_conProductoInexistente_deberiaRetornar404() throws Exception {
        String compraJson = """
            { "productoId": 999, "cantidad": 1 }
        """;

        mockMvc.perform(post("/inventario/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].detail").value("Producto no existe en inventario"));
    }
}


