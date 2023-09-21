package com.backend.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mqz
 */
@Slf4j
@Configuration
public class MinioConfig {


    @Value("${spring.minio.endpoint}")
    private String endpoint;

    @Value("${spring.minio.username}")
    private String username;

    @Value("${spring.minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        log.info("初始化minio");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }

}
