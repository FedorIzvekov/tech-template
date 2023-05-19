package com.fedorizvekov.minio.service;

import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Interface for managing buckets in S3 storage
 */
public interface BucketService {

    /**
     * Creates a new bucket in the S3 storage with the given name.
     *
     * @param bucket - the name of the bucket to be created
     * @throws GeneralSecurityException - if there is an error with the security setup
     * @throws IOException              - if there is an issue with the input/output operations while working with the S3 storage
     * @throws MinioException           - if there is an issue with the MinIO storage service
     */
    void createBucket(String bucket) throws GeneralSecurityException, IOException, MinioException;

}
