package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Banner;
import com.jpetstore.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public Result<List<Banner>> getAllBanners() {
        return Result.success(bannerService.getAllBanners());
    }

    @GetMapping("/active")
    public Result<List<Banner>> getActiveBanners() {
        return Result.success(bannerService.getActiveBanners());
    }

    @PostMapping
    public Result<String> createBanner(
            @RequestParam String title,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String link,
            @RequestParam(required = false, defaultValue = "0") Integer sortOrder,
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @RequestParam(required = false) MultipartFile image) {
        Banner banner = new Banner();
        banner.setTitle(title);
        banner.setSubtitle(subtitle);
        banner.setLink(link);
        banner.setSortOrder(sortOrder);
        banner.setActive(active);

        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String newFilename = "banner_" + UUID.randomUUID().toString() + extension;
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                banner.setImage("/uploads/" + newFilename);
            } catch (IOException e) {
                return Result.error(500, "图片上传失败: " + e.getMessage());
            }
        }

        return bannerService.insertBanner(banner) > 0
                ? Result.success("创建成功", "创建成功")
                : Result.error(500, "创建失败");
    }

    @PutMapping("/{id}")
    public Result<String> updateBanner(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String link,
            @RequestParam(required = false, defaultValue = "0") Integer sortOrder,
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @RequestParam(required = false) MultipartFile image) {
        Banner existing = bannerService.getBannerById(id);
        if (existing == null) return Result.notFound("轮播图不存在");

        existing.setTitle(title);
        existing.setSubtitle(subtitle);
        existing.setLink(link);
        existing.setSortOrder(sortOrder);
        existing.setActive(active);

        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String newFilename = "banner_" + UUID.randomUUID().toString() + extension;
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existing.setImage("/uploads/" + newFilename);
            } catch (IOException e) {
                return Result.error(500, "图片上传失败: " + e.getMessage());
            }
        }

        return bannerService.updateBanner(existing) > 0
                ? Result.success("更新成功", "更新成功")
                : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteBanner(@PathVariable Integer id) {
        return bannerService.deleteBanner(id) > 0
                ? Result.success("删除成功", "删除成功")
                : Result.error(500, "删除失败");
    }
}
