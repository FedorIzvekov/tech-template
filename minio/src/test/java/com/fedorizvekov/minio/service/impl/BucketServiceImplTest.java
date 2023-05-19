package com.fedorizvekov.minio.service.impl;

import static org.mockito.Mockito.verify;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BucketServiceImplTest {

    @InjectMocks
    private BucketServiceImpl bucketService;

    @Mock
    private MinioClient minioClient;


    @Test
    @DisplayName("Should invoke createBucket")
    public void should_invoke_createBucket() throws Exception {
        var bucket = "test-bucket";
        var makeBucketArgs = MakeBucketArgs.builder().bucket(bucket).build();

        bucketService.createBucket(bucket);

        verify(minioClient).makeBucket(makeBucketArgs);
    }

}
