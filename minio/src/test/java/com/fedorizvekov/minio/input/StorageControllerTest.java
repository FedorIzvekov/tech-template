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
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
class StorageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BucketService bucketService;
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
