package com.backend.service.impl;

import com.backend.entity.dto.Account;
import com.backend.mapper.AccountMapper;
import com.backend.service.ImagesService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author mqz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImagesServiceImpl implements ImagesService {

    private final MinioClient minioClient;

    private final AccountMapper accountMapper;

    @Override
    public String updateAvatar(Integer userId, MultipartFile file) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        imageName = "/avatar/" + imageName;

        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(objectArgs);
            int update = accountMapper.
                    update(null, Wrappers.<Account>update().
                            eq("id", userId).set("avatar", imageName));
            if (update > 0) return imageName;
            else return null;
        } catch (Exception e) {
            log.error("图片上传出现问题:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public void fetchImageMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(image)
                .build();
        GetObjectResponse object = minioClient.getObject(args);
        IOUtils.copy(object, stream);
    }
}
