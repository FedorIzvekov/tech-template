package com.fedorizvekov.minio.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageConfigurationProperties.class)
public class MinioClientConfiguration {

    private final StorageConfigurationProperties properties;
    private final OkHttpClient okHttpClient;

    @Bean
    public MinioClient getMinioClient() {

        var credentials = properties.getCredentials();

        return MinioClient.builder()
            .endpoint(credentials.getHost(), credentials.getPort(), credentials.isEnableHttps())
            .credentials(credentials.getAccessKey(), credentials.getSecretKey())
            .httpClient(okHttpClient)
            .build();
    }

}
