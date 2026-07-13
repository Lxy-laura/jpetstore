package com.jpetstore.mapper;

import com.jpetstore.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    Product getProductById(String productid);

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String category);

    List<Product> searchProducts(String keyword);

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteProduct(String productid);

    int updateProductStatus(@Param("productid") String productid, @Param("status") String status);
}