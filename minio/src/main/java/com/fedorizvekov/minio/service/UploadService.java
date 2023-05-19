package com.fedorizvekov.minio.service;

import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Interface for uploading objects to s3 storage
 */
public interface UploadService {

    /**
     * Uploads the specified object to the given s3 storage bucket.
     *
     * @param bucket   - the name of the s3 storage bucket where the object will be uploaded
     * @param object   - the key of the s3 storage object to upload
     * @param request  - HTTP request object containing the input stream of the object to be uploaded
     * @param response - HTTP response object for sending the upload result
     * @throws GeneralSecurityException - if there is an error with the security setup
     * @throws IOException              - if there is an issue with the input/output operations while working with the S3 storage
     * @throws MinioException           - if there is an issue with the MinIO storage service
     */
    void uploadObject(String bucket, String object, HttpServletRequest request, HttpServletResponse response)
        throws GeneralSecurityException, IOException, MinioException;

}
