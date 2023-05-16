package com.fedorizvekov.minio.input;

import com.fedorizvekov.minio.service.BucketService;
import com.fedorizvekov.minio.service.DownloadService;
import com.fedorizvekov.minio.service.UploadService;
import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Log4j2
@Validated
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class StorageController {

    private final BucketService bucketService;
    private final UploadService uploadService;
    private final DownloadService downloadService;

    private final String bucketRegexp = "^[a-z0-9][a-z0-9\\-]{1,61}[a-z0-9]$";
    private final String bucketRegexpMsg = "Bucket name should be in lowercase and include alphanumeric characters or hyphens, "
        + "but not start or end with a hyphen. The bucket name must be between 3 and 63 characters in length.";
    private final String objectRegexp = "^([a-zA-Z0-9]{1}|[a-zA-Z0-9][\\w.-]{0,1022}[a-zA-Z0-9])$";
    private final String objectRegexpMsg = "The object name must be unique within the bucket, can only contain lowercase and uppercase letters, "
        + "digits, hyphens (-), underscores (_), and dots (.), and must begin and end with a letter or number. "
        + "The object name must be between 1 and 1024 characters in length.";
    private final String objectRangeRegexp = "^bytes=(\\d+-\\d+)$";
    private final String objectRangeRegexpMsg = "Object range must be of the format bytes=beginByte-endByte (bytes=0-100).";


    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PutMapping("/{bucket}")
    public void createBucket(@Pattern(regexp = bucketRegexp, message = bucketRegexpMsg) @PathVariable String bucket)
        throws GeneralSecurityException, IOException, MinioException {

        log.info(() -> "REQUEST PUT endpoint /" + bucket);
        bucketService.createBucket(bucket);
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{bucket}/{object}")
    public void uploadObject(
        @Pattern(regexp = bucketRegexp, message = bucketRegexpMsg) @PathVariable String bucket,
        @Pattern(regexp = objectRegexp, message = objectRegexpMsg) @PathVariable String object,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws GeneralSecurityException, IOException, MinioException {

        log.info(() -> "REQUEST PUT endpoint /" + bucket + "/" + object);
        uploadService.uploadObject(bucket, object, request, response);
    }


    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/{bucket}/{object}")
    public StreamingResponseBody downloadObject(
        @Pattern(regexp = bucketRegexp, message = bucketRegexpMsg) @PathVariable String bucket,
        @Pattern(regexp = objectRegexp, message = objectRegexpMsg) @PathVariable String object,
        @Pattern(regexp = objectRangeRegexp, message = objectRangeRegexpMsg) @RequestHeader(required = false) String range,
        HttpServletResponse response
    ) throws GeneralSecurityException, IOException, MinioException {

        log.info(() -> "REQUEST GET endpoint /" + bucket + "/" + object);
        return downloadService.downloadObject(bucket, object, range, response);

    }

}
