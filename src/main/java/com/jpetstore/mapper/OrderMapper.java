import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Order;
import com.jpetstore.domain.OrderItem;
import com.jpetstore.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getAllOrders();

    List<Order> getOrdersByUserId(String userid);

    Order getOrderById(Integer orderid);

    List<OrderItem> getOrderItemsByOrderId(Integer orderid);

    int insertOrderItem(OrderItem item);

    int insertOrderStatus(OrderStatus status);

    int updateOrderStatus(@Param("orderid") Integer orderid, @Param("status") String status);
}

