package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.service.ImagesService;
import io.minio.errors.ErrorResponseException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mqz
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ObjectController {


    private final ImagesService imagesService;

    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpg");
        fetchImage(request, response);
    }

    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream outputStream = response.getOutputStream();
        if (imagePath.length() <= 13) {
            response.setStatus(404);
            outputStream.println(RestBean.failure(404, "Not Found").toString());
        } else {
            try {
                imagesService.fetchImageMinio(outputStream, imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if (e.response().code() == 404) {
                    response.setStatus(404);
                } else {
                    log.error("获取图片失败{}", e.getMessage());
                }
            }
        }
    }

}
