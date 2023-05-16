package com.fedorizvekov.minio.output;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fedorizvekov.minio.service.BucketService;
import com.fedorizvekov.minio.service.DownloadService;
import com.fedorizvekov.minio.service.UploadService;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
class StorageExceptionHandlerTest {

    private static final String BUCKET = "test-bucket";
    private static final String OBJECT = "test-object";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BucketService bucketService;
    @MockBean
    private UploadService uploadService;
    @MockBean
    private DownloadService downloadService;


    private static Stream<Arguments> provideRequestBuilders() {
        return Stream.of(
            Arguments.of(MockMvcRequestBuilders.put("/{bucket}", BUCKET)),
            Arguments.of(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/{bucket}/{object}", BUCKET, OBJECT)
                .file(new MockMultipartFile(OBJECT, "test object binary data".getBytes()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            Arguments.of(MockMvcRequestBuilders.get("/{bucket}/{object}", BUCKET, OBJECT))
        );
    }


    @ParameterizedTest
    @MethodSource("provideRequestBuilders")
    @DisplayName("Should handle GeneralSecurityException")
    public void should_handle_GeneralSecurityException(RequestBuilder requestBuilder) throws Exception {
        var doThrow = doThrow(new GeneralSecurityException("Missing of HMAC SHA-256 library"));

        doThrow.when(bucketService).createBucket(eq(BUCKET));
        doThrow.when(uploadService).uploadObject(eq(BUCKET), eq(OBJECT), any(HttpServletRequest.class), any(HttpServletResponse.class));
        doThrow.when(downloadService).downloadObject(eq(BUCKET), eq(OBJECT), isNull(), any(HttpServletResponse.class));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isForbidden(),
            jsonPath("$.status").value("FORBIDDEN"),
            jsonPath("$.message").value("RESPONSE ERROR, because: GeneralSecurityException: Missing of HMAC SHA-256 library")
        );
    }


    @ParameterizedTest
    @MethodSource("provideRequestBuilders")
    @DisplayName("Should handle IOException")
    public void should_handle_IOException(RequestBuilder requestBuilder) throws Exception {
        var doThrow = doThrow(new IOException("I/O error on S3 operation"));

        doThrow.when(bucketService).createBucket(eq(BUCKET));
        doThrow.when(uploadService).uploadObject(eq(BUCKET), eq(OBJECT), any(HttpServletRequest.class), any(HttpServletResponse.class));
        doThrow.when(downloadService).downloadObject(eq(BUCKET), eq(OBJECT), isNull(), any(HttpServletResponse.class));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isInternalServerError(),
            jsonPath("$.status").value("INTERNAL_SERVER_ERROR"),
            jsonPath("$.message").value("RESPONSE ERROR, because: IOException: I/O error on S3 operation")
        );
    }


    @ParameterizedTest
    @MethodSource("provideRequestBuilders")
    @DisplayName("Should handle MinioException")
    public void should_handle_MinioException(RequestBuilder requestBuilder) throws Exception {
        var doThrow = doThrow(new MinioException("S3 service returned invalid or no error response"));

        doThrow.when(bucketService).createBucket(eq(BUCKET));
        doThrow.when(uploadService).uploadObject(eq(BUCKET), eq(OBJECT), any(HttpServletRequest.class), any(HttpServletResponse.class));
        doThrow.when(downloadService).downloadObject(eq(BUCKET), eq(OBJECT), isNull(), any(HttpServletResponse.class));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value("RESPONSE ERROR, because: MinioException: S3 service returned invalid or no error response")
        );
    }


    @ParameterizedTest
    @MethodSource("provideRequestBuilders")
    @DisplayName("Should handle ValidationException")
    public void should_handle_ValidationException(RequestBuilder requestBuilder) throws Exception {
        var doThrow = doThrow(new ValidationException("Something invalid"));

        doThrow.when(bucketService).createBucket(eq(BUCKET));
        doThrow.when(uploadService).uploadObject(eq(BUCKET), eq(OBJECT), any(HttpServletRequest.class), any(HttpServletResponse.class));
        doThrow.when(downloadService).downloadObject(eq(BUCKET), eq(OBJECT), isNull(), any(HttpServletResponse.class));

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value("RESPONSE ERROR, because: ValidationException: Something invalid")
        );
    }

}
