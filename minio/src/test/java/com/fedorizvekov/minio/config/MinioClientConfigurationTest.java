package com.fedorizvekov.minio.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fedorizvekov.minio.config.StorageConfigurationProperties.Credentials;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MinioClientConfigurationTest {

    private final Credentials credentials = new Credentials();

    @InjectMocks
    private MinioClientConfiguration configuration;
    @Mock
    private StorageConfigurationProperties properties;
    @Mock
    private OkHttpClient okHttpClient;


    @Test
    @DisplayName("Should create MinioClient")
    public void should_create_MinioClient() {
        credentials.setHost("1.1.1.1");
        credentials.setPort(8080);
        credentials.setEnableHttps(true);
        credentials.setAccessKey("access_key");
        credentials.setSecretKey("secret_key");
        when(properties.getCredentials()).thenReturn(credentials);

        var result = configuration.getMinioClient();

        assertThat(result)
            .isNotNull()
            .hasNoNullFieldsOrProperties()
            .isInstanceOf(MinioClient.class);
    }

}
