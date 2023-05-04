package com.fedorizvekov.minio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Getter
@Setter
@Primary
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageConfigurationProperties {

    private Credentials credentials;
    private SSL ssl;
    private Timeout timeout;

    @Getter
    @Setter
    public static class Credentials {
        private String host;
        private int port;
        private boolean enableHttps;
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class SSL {
        private String keyAlias;
        private String keyStoreType;
        private String keyStore;
        private String keyStorePassword;
        private String trustStore;
        private String trustStorePassword;
    }

    @Getter
    @Setter
    public static class Timeout {
        private long connectMinutes;
        private long writeMinutes;
        private long readMinutes;
    }

}
