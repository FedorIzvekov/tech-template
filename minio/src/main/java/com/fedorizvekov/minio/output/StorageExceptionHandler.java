package com.fedorizvekov.minio.output;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fedorizvekov.minio.model.dto.ServerError;
import io.minio.errors.MinioException;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@ControllerAdvice
public class StorageExceptionHandler {

    @ExceptionHandler(GeneralSecurityException.class)
    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    public ServerError handlerGeneralSecurityException(GeneralSecurityException exception) {
        var errorMessage = "RESPONSE ERROR, because: GeneralSecurityException: " + exception.getMessage();
        log.error(() -> errorMessage);
        return new ServerError(FORBIDDEN, errorMessage);
    }


    @ExceptionHandler(IOException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServerError handlerIOException(IOException exception) {
        var errorMessage = "RESPONSE ERROR, because: IOException: " + exception.getMessage();
        log.error(() -> errorMessage);
        return new ServerError(INTERNAL_SERVER_ERROR, errorMessage);
    }


    @ExceptionHandler(MinioException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ServerError handlerMinioException(MinioException exception) {
        var errorMessage = "RESPONSE ERROR, because: MinioException: " + exception.getMessage();
        log.error(() -> errorMessage);
        return new ServerError(BAD_REQUEST, errorMessage);
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ServerError handlerValidationException(ValidationException exception) {
        var errorMessage = "RESPONSE ERROR, because: ValidationException: " + exception.getMessage();
        log.error(() -> errorMessage);
        return new ServerError(BAD_REQUEST, errorMessage);
    }

}
