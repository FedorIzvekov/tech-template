package com.fedorizvekov.minio.output;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fedorizvekov.minio.service.DownloadService;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
class StorageExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private DownloadService downloadService;


    @Test
    @DisplayName("Should handle GeneralSecurityException")
    public void should_handle_GeneralSecurityException() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object";
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object);

        when(downloadService.downloadObject(eq(bucket), eq(object), isNull(), any(HttpServletResponse.class)))
            .thenThrow(new GeneralSecurityException("Missing of HMAC SHA-256 library"));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isForbidden(),
            jsonPath("$.status").value("FORBIDDEN"),
            jsonPath("$.message").value("RESPONSE ERROR, because: GeneralSecurityException: Missing of HMAC SHA-256 library")
        );
    }


    @Test
    @DisplayName("Should handle IOException")
    public void should_handle_IOException() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object";
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object);

        when(downloadService.downloadObject(eq(bucket), eq(object), isNull(), any(HttpServletResponse.class)))
            .thenThrow(new IOException("I/O error on S3 operation"));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isInternalServerError(),
            jsonPath("$.status").value("INTERNAL_SERVER_ERROR"),
            jsonPath("$.message").value("RESPONSE ERROR, because: IOException: I/O error on S3 operation")
        );
    }


    @Test
    @DisplayName("Should handle MinioException")
    public void should_handle_MinioException() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object";
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object);

        when(downloadService.downloadObject(eq(bucket), eq(object), isNull(), any(HttpServletResponse.class)))
            .thenThrow(new MinioException("S3 service returned invalid or no error response"));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value("RESPONSE ERROR, because: MinioException: S3 service returned invalid or no error response")
        );
    }


    @Test
    @DisplayName("Should handle ValidationException")
    public void should_handle_ValidationException() throws Exception {
        var bucket = "test-bucket";
        var object = "test-object";
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object);

        when(downloadService.downloadObject(eq(bucket), eq(object), isNull(), any(HttpServletResponse.class)))
            .thenThrow(new ValidationException("Something invalid"));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value("RESPONSE ERROR, because: ValidationException: Something invalid")
        );
    }

}
