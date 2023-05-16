package com.fedorizvekov.minio.input;

import static java.util.Objects.nonNull;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fedorizvekov.minio.service.BucketService;
import com.fedorizvekov.minio.service.DownloadService;
import com.fedorizvekov.minio.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BucketService bucketService;
    @MockBean
    private UploadService uploadService;
    @MockBean
    private DownloadService downloadService;


    @Test
    @DisplayName("Should invoke createBucket and return CREATED")
    public void should_invoke_createBucket_and_return_CREATED() throws Exception {
        var bucket = "test-bucket";
        var requestBuilder = MockMvcRequestBuilders.put("/{bucket}", bucket);

        mockMvc.perform(requestBuilder).andExpect(status().isCreated());
        verify(bucketService).createBucket(bucket);
    }


    @ParameterizedTest
    @ValueSource(strings = {"12", "123456789-123456789-123456789-123456789-123456789-123456789-1234", "Test-Bucket", "test_bucket", "-test-bucket"})
    @DisplayName("Should handle createBucket with invalid bucket name")
    public void should_handle_createBucket_with_invalid_bucket_name(String bucket) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put("/{bucket}", bucket);

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value(containsString(
                "Bucket name should be in lowercase and include alphanumeric characters or hyphens, "
                    + "but not start or end with a hyphen. The bucket name must be between 3 and 63 characters in length."))
        );
        verify(bucketService, never()).createBucket(anyString());
    }


    @ParameterizedTest
    @ValueSource(strings = {"F", "1", "test-Object_1.jpg"})
    @DisplayName("Should invoke uploadObject and return OK")
    public void should_invoke_uploadObject_and_return_OK(String object) throws Exception {
        var bucket = "test-bucket";
        var file = new MockMultipartFile(object, "test object binary data".getBytes());
        var requestBuilder = MockMvcRequestBuilders
            .multipart(HttpMethod.PUT, "/{bucket}/{object}", bucket, object)
            .file(file)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
        verify(uploadService).uploadObject(eq(bucket), eq(object), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }


    @ParameterizedTest
    @ValueSource(strings = {".object", "object.", "-object", "object-", "_object", "object_", "test@object"})
    @DisplayName("Should handle uploadObject with invalid object name")
    public void should_handle_uploadObject_with_invalid_object_name(String object) throws Exception {
        var bucket = "test-bucket";
        var file = new MockMultipartFile(object, "test object binary data".getBytes());
        var requestBuilder = MockMvcRequestBuilders
            .multipart(HttpMethod.PUT, "/{bucket}/{object}", bucket, object)
            .file(file)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("BAD_REQUEST"),
                jsonPath("$.message").value(containsString("The object name must be unique within the bucket, "
                    + "can only contain lowercase and uppercase letters, digits, hyphens (-), underscores (_), and dots (.), "
                    + "and must begin and end with a letter or number. The object name must be between 1 and 1024 characters in length."))
            );

        verify(bucketService, never()).createBucket(anyString());
    }


    @ParameterizedTest
    @CsvSource(value = {"bucket1 : object1 :", "test-bucket : test-object_1.jpg :", "bucket : object.doc : bytes=0-1000"}, delimiter = ':')
    public void should_invoke_downloadObject_and_return_OK(String bucket, String object, String range) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object);
        if (nonNull(range)) requestBuilder.header("range", range);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
        verify(downloadService).downloadObject(eq(bucket), eq(object), eq(range), any(HttpServletResponse.class));
    }


    @ParameterizedTest
    @ValueSource(strings = {"invalid_range", "bytes=-500", "  bytes=0-600,601-999"})
    @DisplayName("Should handle downloadObject with invalid range format")
    public void should_handle_downloadObject_with_invalid_range_format(String range) throws Exception {
        var bucket = "bucket";
        var object = "object";
        var requestBuilder = MockMvcRequestBuilders.get("/{bucket}/{object}", bucket, object).header("range", range);

        mockMvc.perform(requestBuilder).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.status").value("BAD_REQUEST"),
            jsonPath("$.message").value(containsString("Object range must be of the format bytes=beginByte-endByte (bytes=0-100)"))
        );
        verify(downloadService, never()).downloadObject(anyString(), anyString(), anyString(), any(HttpServletResponse.class));
    }

}
