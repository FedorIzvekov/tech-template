package com.fedorizvekov.minio.config;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.fedorizvekov.minio.config.StorageConfigurationProperties.SSL;
import com.fedorizvekov.minio.model.exception.StorageConfigurationClientException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageConfigurationProperties.class)
public class StorageConfigurationClient {

    private final StorageConfigurationProperties properties;

    @Bean
    public OkHttpClient getOkHttpClient() {

        try {
            var timeout = properties.getTimeout();
            var builder = new OkHttpClient.Builder()
                .protocols(asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .connectTimeout(timeout.getConnectMinutes(), TimeUnit.MINUTES)
                .writeTimeout(timeout.getWriteMinutes(), TimeUnit.MINUTES)
                .readTimeout(timeout.getReadMinutes(), TimeUnit.MINUTES);

            if (properties.getCredentials().isEnableHttps()) {
                var ssl = properties.getSsl();
                var keyManagers = getKeyManagers(ssl);
                var trustManagers = getTrustManagers(ssl);
                var sslContext = SSLContext.getInstance("TLS");

                sslContext.init(keyManagers, trustManagers, null);
                builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
                builder.connectionSpecs(singletonList(ConnectionSpec.RESTRICTED_TLS));
            } else {
                builder.connectionSpecs(singletonList(ConnectionSpec.CLEARTEXT));
            }

            return builder.build();

        } catch (GeneralSecurityException | IOException exception) {
            var errorMessage = "Error creating OkHttpClient, because: " + exception.getMessage();
            log.error(() -> errorMessage);
            throw new StorageConfigurationClientException(errorMessage, exception);
        }
    }


    private TrustManager[] getTrustManagers(SSL ssl) throws GeneralSecurityException, IOException {
        var trustStore = createKeyStore(ssl.getKeyStoreType(), ssl.getTrustStore(), ssl.getTrustStorePassword());
        var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }


    private KeyManager[] getKeyManagers(SSL ssl) throws GeneralSecurityException, IOException {
        var keyStore = createKeyStore(ssl.getKeyStoreType(), ssl.getKeyStore(), ssl.getKeyStorePassword());
        var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, ssl.getKeyStorePassword().toCharArray());
        return keyManagerFactory.getKeyManagers();
    }


    private KeyStore createKeyStore(String type, String path, String password) throws GeneralSecurityException, IOException {
        try (var inputStream = new FileInputStream(path)) {
            var keyStore = KeyStore.getInstance(type);
            keyStore.load(inputStream, password.toCharArray());
            return keyStore;
        }
    }

}
