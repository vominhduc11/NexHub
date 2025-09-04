package com.devwonder.product_service.controller;

import com.devwonder.product_service.dto.ProductRequest;
import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_WithAdminRole_ShouldReturnCreated() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setCategoryId(1L);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Test Product");
        response.setSku("TEST-001");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/product/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Gateway-Request", "true")
                        .header("X-User-Roles", "ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.sku").value("TEST-001"));
    }

    @Test
    void createProduct_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setCategoryId(1L);

        // When & Then
        mockMvc.perform(post("/product/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Gateway-Request", "true")
                        .header("X-User-Roles", "CUSTOMER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_WithoutGatewayHeader_ShouldReturnForbidden() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setCategoryId(1L);

        // When & Then
        mockMvc.perform(post("/product/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Roles", "ADMIN"))
                .andExpect(status().isForbidden());
    }
}