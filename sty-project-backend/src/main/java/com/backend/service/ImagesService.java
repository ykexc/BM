package com.backend.service;

import com.backend.entity.dto.StoreImage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author mqz
 */
public interface ImagesService extends IService<StoreImage> {


    String updateAvatar(Integer userId, MultipartFile file) throws IOException;

    String uploadImage(Integer userId, MultipartFile file) throws IOException;


    void fetchImageMinio(OutputStream stream, String image) throws Exception;

}
