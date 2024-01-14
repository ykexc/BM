package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.service.ImagesService;
import com.backend.utils.Const;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author mqz
 */
@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImagesController {

    private final ImagesService imagesService;

    @RequestMapping("/avatar")
    public RestBean<String> uploadAvatar
            (@RequestParam("file") MultipartFile file,
             @RequestAttribute(Const.ATTR_USER_ID) Integer id
            ) throws IOException {
        if (file.getSize() > 1024 * 100)
            return RestBean.failure(400, "头像图片不能大于100kb");
        log.info("头像正在上传");
        String url = imagesService.updateAvatar(id, file);
        if (url != null) {
            log.info("头像上传成功, 大小: " + file.getSize());
            return RestBean.success(url);
        } else {
            return RestBean.failure(400, "头像上传失败");
        }
    }

    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file") MultipartFile file,
                                        @RequestAttribute(Const.ATTR_USER_ID) Integer id,
                                        HttpServletResponse response
    ) throws IOException {
        if (file.getSize() > 1024 * 1024 * 5) {
            return RestBean.failure(400, "头像图片不能大于5MB");
        }
        log.info("图片正在上传");
        String url = imagesService.uploadImage(id, file);
        if (url != null) {
            log.info("图片上传成功, 大小: " + file.getSize());
            return RestBean.success(url);
        } else {
            response.setStatus(400);
            return RestBean.failure(400, "图片上传失败");
        }
    }




}
