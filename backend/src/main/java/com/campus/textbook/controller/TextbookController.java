package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Textbook;
import com.campus.textbook.service.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 教材相关接口（发布、检索、详情）
 */
@RestController
@RequestMapping("/api/textbook")
public class TextbookController {

    @Autowired
    private TextbookService textbookService;

    @Value("${upload.path:/app/uploads/}")
    private String uploadPath;

    /**
     * 多条件检索教材（TC-SEARCH-001~005）
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String isbn,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize) {
        return textbookService.searchTextbooks(keyword, isbn, minPrice, maxPrice, page, pageSize);
    }

    /**
     * 教材详情
     */
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        return textbookService.getDetail(id);
    }

    /**
     * 发布教材（论文5.1.2，TC-PUBLISH-001~005）
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody Textbook textbook, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return textbookService.publishTextbook(textbook, userId);
    }

    /**
     * 上传教材图片
     */
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("请选择要上传的图片");
        String originalName = file.getOriginalFilename();
        if (originalName == null) return Result.error("文件名不合法");
        String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        // 校验图片格式（TC-PUBLISH-004）
        if (!suffix.equals(".jpg") && !suffix.equals(".jpeg")
                && !suffix.equals(".png") && !suffix.equals(".gif")) {
            return Result.error("请上传图片格式文件");
        }
        // 校验文件大小（TC-PUBLISH-005）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小过大，请上传5MB以内的图片");
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        File destDir = new File(uploadPath);
        if (!destDir.exists()) destDir.mkdirs();
        file.transferTo(new File(uploadPath + filename));
        return Result.success("上传成功", "/uploads/" + filename);
    }

    /**
     * 查询我发布的教材
     */
    @GetMapping("/my")
    public Result myPublished(Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return textbookService.getMyPublished(userId);
    }

    /**
     * 用户下架自己的教材
     */
    @PutMapping("/offshelf/{id}")
    public Result offShelf(@PathVariable Integer id, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return textbookService.offShelfByUser(id, userId);
    }
}
