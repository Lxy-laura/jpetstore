package com.jpetstore.service;

import com.jpetstore.domain.Banner;
import com.jpetstore.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    public List<Banner> getAllBanners() {
        return bannerMapper.selectList(null);
    }

    public List<Banner> getActiveBanners() {
        return bannerMapper.getActiveBanners();
    }

    public Banner getBannerById(Integer id) {
        return bannerMapper.selectById(id);
    }

    public int insertBanner(Banner banner) {
        return bannerMapper.insert(banner);
    }

    public int updateBanner(Banner banner) {
        return bannerMapper.updateById(banner);
    }

    public int deleteBanner(Integer id) {
        return bannerMapper.deleteById(id);
    }
}

