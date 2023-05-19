package com.fedorizvekov.minio.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = StorageConfigurationProperties.class)
@TestPropertySource(properties = {
    "storage.credentials.host = 127.0.0.1",
    "storage.credentials.port = 9000",
    "storage.credentials.enable-https = true",
    "storage.credentials.access-key = access_key",
    "storage.credentials.secret-key = secret_key",

    "storage.ssl.key-alias = alias",
    "storage.ssl.key-store-type = PKCS12",
    "storage.ssl.key-store = /path/to/certs/keystore.p12",
    "storage.ssl.key-store-password = test_keystore_password",
    "storage.ssl.trust-store = /path/to/certs/truststore.p12",
    "storage.ssl.trust-store-password = test_truststore_password",

    "storage.timeout.connect-minutes = 30",
    "storage.timeout.write-minutes = 30",
    "storage.timeout.read-minutes = 30"
})
class StorageConfigurationPropertiesTest {

    @Autowired
    private StorageConfigurationProperties properties;


    @Test
    @DisplayName("Should create valid Credentials properties")
    void should_create_valid_credentials() {
        var credentials = properties.getCredentials();
        assertAll(
            () -> assertThat(credentials).hasNoNullFieldsOrProperties(),
            () -> assertThat(credentials.getHost()).isEqualTo("127.0.0.1"),
            () -> assertThat(credentials.getPort()).isEqualTo(9000),
            () -> assertThat(credentials.isEnableHttps()).isTrue(),
            () -> assertThat(credentials.getAccessKey()).isEqualTo("access_key"),
            () -> assertThat(credentials.getSecretKey()).isEqualTo("secret_key")
        );
    }


    @Test
    @DisplayName("Should create valid SSL properties")
    void should_create_valid_ssl() {
        var ssl = properties.getSsl();
        assertAll(
            () -> assertThat(ssl).hasNoNullFieldsOrProperties(),
            () -> assertThat(ssl.getKeyStoreType()).isEqualTo("PKCS12"),
            () -> assertThat(ssl.getKeyAlias()).isEqualTo("alias"),
            () -> assertThat(ssl.getKeyStore()).isEqualTo("/path/to/certs/keystore.p12"),
            () -> assertThat(ssl.getKeyStorePassword()).isEqualTo("test_keystore_password"),
            () -> assertThat(ssl.getTrustStore()).isEqualTo("/path/to/certs/truststore.p12"),
            () -> assertThat(ssl.getTrustStorePassword()).isEqualTo("test_truststore_password")
        );
    }


    @Test
    @DisplayName("Should create valid Timeout properties")
    void should_create_valid_timeout() {
        var timeout = properties.getTimeout();
        assertAll(
            () -> assertThat(timeout).hasNoNullFieldsOrProperties(),
            () -> assertThat(timeout.getConnectMinutes()).isEqualTo(30L),
            () -> assertThat(timeout.getWriteMinutes()).isEqualTo(30L),
            () -> assertThat(timeout.getReadMinutes()).isEqualTo(30L)
        );
    }

}