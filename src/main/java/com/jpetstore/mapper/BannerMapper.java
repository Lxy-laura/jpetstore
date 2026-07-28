package com.jpetstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    @Select("SELECT * FROM banner WHERE active = true ORDER BY sort_order ASC")
    List<Banner> getActiveBanners();
    default List<Banner> getAllBanners() { return selectList(null); }
    default int insertBanner(Banner banner) { return insert(banner); }
    default int updateBanner(Banner banner) { return updateById(banner); }
    default int deleteBanner(Integer id) { return deleteById(id); }
}
