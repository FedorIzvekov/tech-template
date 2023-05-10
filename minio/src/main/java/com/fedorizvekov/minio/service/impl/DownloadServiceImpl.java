package com.fedorizvekov.minio.service.impl;

import static java.util.Objects.nonNull;

import com.fedorizvekov.minio.service.DownloadService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Log4j2
@Service
@RequiredArgsConstructor
public class DownloadServiceImpl implements DownloadService {

    private final MinioClient minioClient;


    @Override
    public StreamingResponseBody downloadObject(String bucket, String object, String range, HttpServletResponse response)
        throws GeneralSecurityException, IOException, MinioException {

        var getObjectArgsBuilder = GetObjectArgs.builder()
            .bucket(bucket)
            .object(object);

        if (nonNull(range)) {
            var rangeValues = range.split("=")[1].split("-");
            var startByte = Long.parseLong(rangeValues[0]);
            var endByte = Long.parseLong(rangeValues[1]);

            getObjectArgsBuilder.offset(startByte);
            getObjectArgsBuilder.length(endByte);
        }

        var getObjectResponse = minioClient.getObject(getObjectArgsBuilder.build());

        getObjectResponse.headers().forEach(header -> response.setHeader(header.getFirst(), header.getSecond()));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + getObjectResponse.object());

        return streamingResponseBody -> {
            try (getObjectResponse; var outputStream = response.getOutputStream()) {
                IOUtils.copyLarge(getObjectResponse, outputStream);
                response.flushBuffer();
                log.info(() -> "File with name '" + getObjectResponse.object() + "' success downloaded");
            }
        };
    }

}
