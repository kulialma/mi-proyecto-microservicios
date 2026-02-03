package com.example.micro_productos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearProducto_deberiaRetornar201() throws Exception {
        String json = """
            {
              "nombre": "Laptop",
              "precio": 1200.0,
              "descripcion": "Laptop de prueba"
            }
        """;

        mockMvc.perform(post("/productos")
                .header("X-API-KEY", "MI_API_KEY_SECRETA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.nombre").value("Laptop"));
    }

    @Test
    void obtenerProductoInexistente_deberiaRetornar404() throws Exception {
        mockMvc.perform(get("/productos/999")
                .header("X-API-KEY", "MI_API_KEY_SECRETA"))
                .andExpect(status().isNotFound());
    }
}

