package com.fedorizvekov.minio.service.impl;

import com.fedorizvekov.minio.service.UploadService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final MinioClient minioClient;

    @Override
    public void uploadObject(String bucket, String object, HttpServletRequest request, HttpServletResponse response)
        throws GeneralSecurityException, IOException, MinioException {

        try (InputStream inputStream = request.getInputStream()) {

            var objectSize = request.getContentLengthLong();

            var putObjectArgs = PutObjectArgs.builder()
                .bucket(bucket)
                .object(object)
                .stream(inputStream, objectSize, -1)
                .contentType(URLConnection.guessContentTypeFromName(object))
                .build();

            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            objectWriteResponse.headers().forEach(header -> response.setHeader(header.getFirst(), header.getSecond()));
            response.setHeader("Content-Length", "0");
            log.info(() -> "File with name '" + objectWriteResponse.object() + "' success uploaded");
        }
    }

}
