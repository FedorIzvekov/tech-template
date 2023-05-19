package com.fedorizvekov.minio.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import okhttp3.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {

    @InjectMocks
    private UploadServiceImpl service;

    @Mock
    private MinioClient minioClient;
    @Mock
    private ObjectWriteResponse objectWriteResponse;
    @Mock
    private MockHttpServletRequest request;
    @Mock
    private MockHttpServletResponse response;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;


    @Test
    @DisplayName("Should invoke putObject and set headers to ServletResponse")
    void should_invoke_putObject_and_set_headers_to_ServletResponse() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object.jpg";
        var data = "test object data".getBytes();

        when(request.getContentLengthLong()).thenReturn((long) data.length);
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(data)));
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(objectWriteResponse);
        when(objectWriteResponse.headers()).thenReturn(Headers.of("ETag", "408a556f4ca9b7c702f1d50049090a18"));

        service.uploadObject(bucket, object, request, response);

        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        assertAll(
            () -> assertThat(putObjectArgsCaptor.getValue().bucket()).isEqualTo(bucket),
            () -> assertThat(putObjectArgsCaptor.getValue().object()).isEqualTo(object),
            () -> assertThat(putObjectArgsCaptor.getValue().stream().readAllBytes()).isEqualTo(data),
            () -> assertThat(putObjectArgsCaptor.getValue().objectSize()).isEqualTo(data.length)
        );

        verify(response).setHeader("ETag", "408a556f4ca9b7c702f1d50049090a18");
        verify(response).setHeader("Content-Length", "0");
    }

}
