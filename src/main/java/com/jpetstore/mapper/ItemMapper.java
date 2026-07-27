import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {

    @Select("SELECT * FROM item WHERE productid = #{productid}")
    List<Item> getItemsByProductId(String productid);

    @Update("UPDATE item SET qty = #{quantity} WHERE itemid = #{itemid}")
    int updateInventory(@Param("itemid") String itemid, @Param("quantity") int quantity);
}
