package com.jpetstore.mapper;

import com.jpetstore.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> getProductsByCategory(@Param("category") String category);

    List<Product> searchProducts(@Param("keyword") String keyword);

    Product getProductById(@Param("productid") String productid);

    List<Product> getAllProducts();

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteProduct(@Param("productid") String productid);
}