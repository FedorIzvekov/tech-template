package com.fedorizvekov.minio.model.exception;

public class StorageConfigurationClientException extends RuntimeException {

    public StorageConfigurationClientException(String message, Exception exception) {
        super(message, exception);
    }

}
