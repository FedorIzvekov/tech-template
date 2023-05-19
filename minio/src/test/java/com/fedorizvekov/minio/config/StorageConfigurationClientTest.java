package com.fedorizvekov.minio.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fedorizvekov.minio.config.StorageConfigurationProperties.Credentials;
import com.fedorizvekov.minio.config.StorageConfigurationProperties.SSL;
import com.fedorizvekov.minio.config.StorageConfigurationProperties.Timeout;
import com.fedorizvekov.minio.model.exception.StorageConfigurationClientException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.Protocol;
import okhttp3.TlsVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageConfigurationClientTest {

    private final Credentials credentials = new Credentials();
    private final SSL ssl = new SSL();
    private final Timeout timeout = new Timeout();

    @InjectMocks
    private StorageConfigurationClient client;
    @Mock
    private StorageConfigurationProperties properties;
    @Mock
    private SSLContext sslContext;
    @Mock
    private SSLSocketFactory sslSocketFactory;

    private MockedStatic<SSLContext> mockedStaticSslContext;

    @BeforeEach
    void setUp() {
        mockedStaticSslContext = mockStatic(SSLContext.class);
    }

    @AfterEach
    void tearDown() {
        mockedStaticSslContext.close();
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 60, 600})
    @DisplayName("Should return OkHttpClient without TLS")
    void should_return_OkHttpClient_without_tls(long minutes) {
        credentials.setEnableHttps(false);
        timeout.setConnectMinutes(minutes);
        timeout.setReadMinutes(minutes);
        timeout.setWriteMinutes(minutes);

        when(properties.getCredentials()).thenReturn(credentials);
        when(properties.getTimeout()).thenReturn(timeout);

        var result = client.getOkHttpClient();

        assertAll(
            () -> assertThat(result.protocols()).contains(Protocol.HTTP_2, Protocol.HTTP_1_1),
            () -> assertThat(result.connectionSpecs()).containsExactly(ConnectionSpec.CLEARTEXT),
            () -> assertThat(result.connectionSpecs().get(0).tlsVersions()).isNull(),
            () -> assertThat(result.connectTimeoutMillis()).isEqualTo(minutes * 60_000),
            () -> assertThat(result.writeTimeoutMillis()).isEqualTo(minutes * 60_000),
            () -> assertThat(result.readTimeoutMillis()).isEqualTo(minutes * 60_000)
        );
        mockedStaticSslContext.verify(() -> SSLContext.getInstance("TLS"), never());
    }


    @Test
    @DisplayName("Should return OkHttpClient with TLS")
    void should_return_OkHttpClient_with_tls() throws Exception {
        credentials.setEnableHttps(true);
        ssl.setKeyStoreType("PKCS12");
        ssl.setKeyStore("src/test/resources/test-store.p12");
        ssl.setKeyStorePassword("test123");
        ssl.setTrustStore("src/test/resources/test-store.p12");
        ssl.setTrustStorePassword("test123");
        timeout.setConnectMinutes(1L);
        timeout.setReadMinutes(1L);
        timeout.setWriteMinutes(1L);

        when(properties.getCredentials()).thenReturn(credentials);
        when(properties.getSsl()).thenReturn(ssl);
        when(properties.getTimeout()).thenReturn(timeout);
        mockedStaticSslContext.when(() -> SSLContext.getInstance("TLS")).thenReturn(sslContext);
        when(sslContext.getSocketFactory()).thenReturn(sslSocketFactory);

        var result = client.getOkHttpClient();

        assertAll(
            () -> assertThat(result.protocols()).contains(Protocol.HTTP_2, Protocol.HTTP_1_1),
            () -> assertThat(result.connectionSpecs()).containsExactly(ConnectionSpec.RESTRICTED_TLS),
            () -> assertThat(result.connectionSpecs().get(0).tlsVersions()).containsExactly(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2),
            () -> assertThat(result.connectTimeoutMillis()).isEqualTo(1 * 60_000),
            () -> assertThat(result.writeTimeoutMillis()).isEqualTo(1 * 60_000),
            () -> assertThat(result.readTimeoutMillis()).isEqualTo(1 * 60_000)
        );
        verify(sslContext).init(any(KeyManager[].class), any(TrustManager[].class), isNull());
    }


    @Test
    @DisplayName("Should handle IOException")
    public void should_handle_IOException() {
        credentials.setEnableHttps(true);
        ssl.setKeyStore("invalid_key_store_path");

        when(properties.getCredentials()).thenReturn(credentials);
        when(properties.getSsl()).thenReturn(ssl);
        when(properties.getTimeout()).thenReturn(timeout);

        assertThatThrownBy(() -> client.getOkHttpClient())
            .isInstanceOf(StorageConfigurationClientException.class)
            .hasCauseInstanceOf(IOException.class)
            .hasMessage("Error creating OkHttpClient, because: invalid_key_store_path (No such file or directory)")
            .hasStackTraceContaining("java.io.FileNotFoundException");
    }


    @Test
    @DisplayName("Should handle GeneralSecurityException")
    public void should_handle_GeneralSecurityException() {
        credentials.setEnableHttps(true);
        ssl.setKeyStoreType("invalid_key_store_type");
        ssl.setKeyStore("src/test/resources/test-store.p12");

        when(properties.getCredentials()).thenReturn(credentials);
        when(properties.getSsl()).thenReturn(ssl);
        when(properties.getTimeout()).thenReturn(timeout);

        assertThatThrownBy(() -> client.getOkHttpClient())
            .isInstanceOf(StorageConfigurationClientException.class)
            .hasCauseInstanceOf(GeneralSecurityException.class)
            .hasMessage("Error creating OkHttpClient, because: invalid_key_store_type not found")
            .hasStackTraceContaining("java.security.NoSuchAlgorithmException");
    }

}
