package com.fedorizvekov.minio.service.impl;

import com.fedorizvekov.minio.service.BucketService;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {

    private final MinioClient minioClient;

    @Override
    public void createBucket(String bucket) throws GeneralSecurityException, IOException, MinioException {
        var makeBucketArgs = MakeBucketArgs.builder()
            .bucket(bucket)
            .build();

        minioClient.makeBucket(makeBucketArgs);
    }

}
