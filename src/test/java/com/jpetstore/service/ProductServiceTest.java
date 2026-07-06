package com.jpetstore.service;

import com.jpetstore.domain.Product;
import com.jpetstore.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setProductid("FI-SW-01");
        product1.setCategory("FISH");
        product1.setName("Angelfish");
        product1.setDescription("Freshwater angelfish");
        product1.setImage("fish1.jpg");

        product2 = new Product();
        product2.setProductid("K9-BD-01");
        product2.setCategory("DOGS");
        product2.setName("Bulldog");
        product2.setDescription("Friendly breed");
        product2.setImage("dog1.jpg");
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> products = Arrays.asList(product1);
        when(productMapper.getProductsByCategory("FISH")).thenReturn(products);

        List<Product> result = productService.getProductsByCategory("FISH");

        assertEquals(1, result.size());
        assertEquals("FI-SW-01", result.get(0).getProductid());
        verify(productMapper, times(1)).getProductsByCategory("FISH");
    }

    @Test
    void testSearchProducts() {
        List<Product> products = Arrays.asList(product1);
        when(productMapper.searchProducts("Angel")).thenReturn(products);

        List<Product> result = productService.searchProducts("Angel");

        assertEquals(1, result.size());
        assertEquals("Angelfish", result.get(0).getName());
        verify(productMapper, times(1)).searchProducts("Angel");
    }

    @Test
    void testSearchProductsNoResult() {
        when(productMapper.searchProducts("NOTHING")).thenReturn(Collections.emptyList());

        List<Product> result = productService.searchProducts("NOTHING");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductById() {
        when(productMapper.getProductById("FI-SW-01")).thenReturn(product1);

        Product result = productService.getProductById("FI-SW-01");

        assertNotNull(result);
        assertEquals("FI-SW-01", result.getProductid());
        assertEquals("Angelfish", result.getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productMapper.getProductById("NOT_EXIST")).thenReturn(null);

        Product result = productService.getProductById("NOT_EXIST");

        assertNull(result);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        when(productMapper.getAllProducts()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
    }

    @Test
    void testInsertProduct() {
        when(productMapper.insertProduct(product1)).thenReturn(1);

        int result = productService.insertProduct(product1);

        assertEquals(1, result);
        verify(productMapper, times(1)).insertProduct(product1);
    }

    @Test
    void testUpdateProduct() {
        product1.setDescription("Updated description");
        when(productMapper.updateProduct(product1)).thenReturn(1);

        int result = productService.updateProduct(product1);

        assertEquals(1, result);
    }

    @Test
    void testDeleteProduct() {
        when(productMapper.deleteProduct("FI-SW-01")).thenReturn(1);

        int result = productService.deleteProduct("FI-SW-01");

        assertEquals(1, result);
    }
}