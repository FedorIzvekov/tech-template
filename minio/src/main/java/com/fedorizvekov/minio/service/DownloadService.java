package com.fedorizvekov.minio.service;

import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Interface for retrieving objects from s3 storage
 */
public interface DownloadService {

    /**
     * Retrieves the specified object from s3 storage returns it as a StreamingResponseBody.
     *
     * @param bucket   - name of the s3 storage bucket where the object is located
     * @param object   - key of the s3 storage object to retrieve
     * @param range    - optional byte range, in the format "bytes=beginByte-endByte", to retrieve part of the object
     * @param response - HTTP response to which the retrieved object's data will be written.
     * @return StreamingResponseBody - functional interface that writes the object's data to the response body
     * @throws GeneralSecurityException - if there is an error with the security setup
     * @throws IOException              - if there is an issue with the input/output operations while working with the S3 storage
     * @throws MinioException           - if there is an issue with the MinIO storage service
     */
    StreamingResponseBody downloadObject(String bucket, String object, String range, HttpServletResponse response)
        throws GeneralSecurityException, IOException, MinioException;
}
