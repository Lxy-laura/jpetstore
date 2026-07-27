import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {

    Product getProductById(String productid);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProducts(String keyword);

    int updateProductStatus(@Param("productid") String productid, @Param("status") String status);
}
