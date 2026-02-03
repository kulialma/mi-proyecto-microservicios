package com.example.micro_productos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.micro_productos.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

class FlujoCompraIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioRepository inventarioRepository;

    @BeforeEach
    void limpiarInventario() {
      inventarioRepository.deleteAll();
    }

    @Test
    void flujoCompraCompleto_deberiaFuncionar() throws Exception {
        // Crear producto
        String productoJson = """
            {
              "nombre": "Mouse",
              "precio": 50.0,
              "descripcion": "Mouse óptico"
            }
        """;

        mockMvc.perform(post("/productos")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.nombre").value("Mouse"));

        // Crear inventario
        String inventarioJson = """
            {
              "productoId": 1,
              "cantidad": 10
            }
        """;

        mockMvc.perform(post("/inventario")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.cantidad").value(10));

        // Realizar compra exitosa
        String compraJson = """
            {
              "productoId": 1,
              "cantidad": 2
            }
        """;

        mockMvc.perform(post("/inventario/compra")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.cantidad").value(2));
    }

    @Test
    void flujoCompra_conStockInsuficiente_deberiaFallar() throws Exception {
        // Crear inventario con 2 unidades
        String inventarioJson = """
            {
              "productoId": 1,
              "cantidad": 2
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
    void flujoCompra_conProductoInexistente_deberiaRetornar404() throws Exception {
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

