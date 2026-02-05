package com.example.micro_productos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.micro_productos.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FlujoCompraIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioRepository inventarioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void limpiarInventario() {
        inventarioRepository.deleteAll();
    }

    @Test
    void flujoCompraCompleto_deberiaFuncionar() throws Exception {
        // 1. Crear producto
        String productoJson = """
            {
              "nombre": "Mouse",
              "precio": 50.0,
              "descripcion": "Mouse óptico"
            }
        """;

        MvcResult productoResult = mockMvc.perform(post("/productos")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productoJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Extraer id del producto creado
        String productoResponse = productoResult.getResponse().getContentAsString();
        JsonNode productoNode = objectMapper.readTree(productoResponse);
        long productoId = productoNode.at("/data/id").asLong();

        // 2. Crear inventario para ese producto
        String inventarioJson = """
            { "productoId": %d, "cantidad": 10 }
        """.formatted(productoId);

        mockMvc.perform(post("/inventario")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.cantidad").value(10));

        // 3. Realizar compra exitosa
        String compraJson = """
            { "productoId": %d, "cantidad": 2 }
        """.formatted(productoId);

        mockMvc.perform(post("/inventario/compras")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.cantidadRestante").value(8));
    }

    @Test
    void flujoCompra_conStockInsuficiente_deberiaFallar() throws Exception {
        // Crear inventario con 2 unidades
        String inventarioJson = """
            { "productoId": 1, "cantidad": 2 }
        """;

        mockMvc.perform(post("/inventario")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inventarioJson))
                .andExpect(status().isCreated());

        // Intentar comprar más de lo disponible
        String compraJson = """
            { "productoId": 1, "cantidad": 5 }
        """;

        mockMvc.perform(post("/inventario/compras")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].detail").value("Inventario insuficiente"));
    }

    @Test
    void flujoCompra_conProductoInexistente_deberiaRetornar404() throws Exception {
        String compraJson = """
            { "productoId": 999, "cantidad": 1 }
        """;

        mockMvc.perform(post("/inventario/compras")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(compraJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].detail").value("Producto no existe en inventario"));
    }
}


