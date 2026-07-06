package com.jpetstore.service;

import com.jpetstore.domain.Product;
import com.jpetstore.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getProductsByCategory(String category) {
        return productMapper.getProductsByCategory(category);
    }

    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }

    public Product getProductById(String productid) {
        return productMapper.getProductById(productid);
    }

    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    @Transactional
    public int insertProduct(Product product) {
        return productMapper.insertProduct(product);
    }

    @Transactional
    public int updateProduct(Product product) {
        return productMapper.updateProduct(product);
    }

    @Transactional
    public int deleteProduct(String productid) {
        return productMapper.deleteProduct(productid);
    }
}