package com.fedorizvekov.minio.model.dto;

import org.springframework.http.HttpStatus;

public record ServerError(HttpStatus status, String message) {}
