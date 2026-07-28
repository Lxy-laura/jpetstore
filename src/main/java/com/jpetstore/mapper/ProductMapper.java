package com.jpetstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    Product getProductById(String productid);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProducts(String keyword);

    int updateProductStatus(@Param("productid") String productid, @Param("status") String status);
    
    default List<Product> getAllProducts() { return selectList(null); }
    default int insertProduct(Product product) { return insert(product); }
    default int updateProduct(Product product) { return updateById(product); }
    default int deleteProduct(String productid) { return deleteById(productid); }
}
