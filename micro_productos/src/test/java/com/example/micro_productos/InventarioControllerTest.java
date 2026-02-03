package com.example.micro_productos;

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

    @Test
    void crearInventario_deberiaRetornar201() throws Exception {
        String json = """
            {
              "productoId": 1,
              "cantidad": 10
            }
        """;

        mockMvc.perform(post("/inventario")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.cantidad").value(10));
    }

    @Test
    void realizarCompra_conStockInsuficiente_deberiaRetornarError() throws Exception {
        // Crear inventario con 1 unidad
        String inventarioJson = """
            {
              "productoId": 1,
              "cantidad": 1
            }
        """;
        mockMvc.perform(post("/inventario")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventarioJson))
                .andExpect(status().isCreated());

        // Intentar comprar más de lo disponible
        String compraJson = """
            {
              "productoId": 1,
              "cantidad": 5
            }
        """;
        mockMvc.perform(post("/inventario/compra")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void realizarCompra_conProductoInexistente_deberiaRetornar404() throws Exception {
        String compraJson = """
            {
              "productoId": 999,
              "cantidad": 1
            }
        """;
        mockMvc.perform(post("/inventario/compra")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isNotFound());
    }
}

