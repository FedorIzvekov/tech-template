package com.fedorizvekov.minio.service.impl;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import jakarta.servlet.ServletOutputStream;
import okhttp3.Headers;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class DownloadServiceImplTest {

    @InjectMocks
    private DownloadServiceImpl service;

    @Mock
    private MinioClient minioClient;
    @Mock
    private GetObjectResponse getObjectResponse;
    @Mock
    private MockHttpServletResponse servletResponse;
    @Mock
    private ServletOutputStream servletOutputStream;

    private MockedStatic<IOUtils> mockedStaticIOUtils;

    @BeforeEach
    void setUp() {
        mockedStaticIOUtils = mockStatic(IOUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedStaticIOUtils.close();
    }


    @Test
    @DisplayName("Should copy all object to ServletResponse")
    void should_copy_all_object_to_ServletResponse() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object.jpg";
        var getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(object).build();
        var headers = Headers.of("Content-Type", "image/jpeg", "Content-Length", "10000");

        when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);
        when(getObjectResponse.headers()).thenReturn(headers);
        when(getObjectResponse.object()).thenReturn(object);
        when(servletResponse.getOutputStream()).thenReturn(servletOutputStream);

        service.downloadObject(bucket, object, null, servletResponse).writeTo(servletOutputStream);

        verify(minioClient).getObject(getObjectArgs);
        verify(servletResponse).setHeader("Content-Disposition", "attachment; filename=test-object.jpg");
        verify(servletResponse).setHeader("Content-Type", "image/jpeg");
        verify(servletResponse).setHeader("Content-Length", "10000");
        mockedStaticIOUtils.verify(() -> IOUtils.copyLarge(getObjectResponse, servletOutputStream));
        verify(servletResponse).flushBuffer();
    }


    @Test
    @DisplayName("Should copy part of object to ServletResponse")
    void should_copy_part_of_object_to_ServletResponse() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object.jpg";
        var offset = 0L;
        var length = 1000L;
        var range = "bytes=" + offset + "-" + length;
        var getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(object).offset(offset).length(length).build();
        var headers = Headers.of("Content-Type", "image/jpeg", "Content-Length", "1000");

        when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);
        when(getObjectResponse.headers()).thenReturn(headers);
        when(getObjectResponse.object()).thenReturn(object);
        when(servletResponse.getOutputStream()).thenReturn(servletOutputStream);

        service.downloadObject(bucket, object, range, servletResponse).writeTo(servletOutputStream);

        verify(minioClient).getObject(getObjectArgs);
        verify(servletResponse).setHeader("Content-Disposition", "attachment; filename=test-object.jpg");
        verify(servletResponse).setHeader("Content-Type", "image/jpeg");
        verify(servletResponse).setHeader("Content-Length", "1000");
        mockedStaticIOUtils.verify(() -> IOUtils.copyLarge(getObjectResponse, servletOutputStream));
        verify(servletResponse).flushBuffer();
    }

}
