package com.backend.service.impl;

import com.backend.entity.dto.Account;
import com.backend.entity.dto.StoreImage;
import com.backend.mapper.AccountMapper;
import com.backend.mapper.ImageStoreMapper;
import com.backend.service.ImagesService;
import com.backend.utils.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.backend.utils.Const.FORUM_IMAGE_COUNTER;

/**
 * @author mqz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImagesServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImagesService {

    private final MinioClient minioClient;

    private final FlowUtil flowUtil;

    private final AccountMapper accountMapper;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

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
            String avatar = accountMapper.selectById(userId).getAvatar();
            this.deleteOldAvatar(avatar);
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

    /**
     * @param userId userId
     * @param file   file
     * @return url
     * @throws IOException 异常处理
     */
    @Override
    public String uploadImage(Integer userId, MultipartFile file) throws IOException {
        String key = FORUM_IMAGE_COUNTER + userId;
        if (!flowUtil.limitPeriodCounterCheck(key, 20, 3600)) {
            return null;
        }
        String imageName = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        imageName = "/cache/" + dateFormat.format(date) + "/" + imageName;
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(objectArgs);
            if (this.save(new StoreImage(userId, imageName, date))) {
                return imageName;
            }
        } catch (Exception e) {
            log.error("图片上传出现问题:{}", e.getMessage());
            return null;
        }
        return null;
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

    private void deleteOldAvatar(String avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs
                .builder()
                .bucket("study")
                .object(avatar)
                .build();
        minioClient.removeObject(remove);
    }

}
