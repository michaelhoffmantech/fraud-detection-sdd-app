package com.globalfraud.frauddetection.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
