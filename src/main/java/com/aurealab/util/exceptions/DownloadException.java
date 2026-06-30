package com.aurealab.util.exceptions;

import org.springframework.http.HttpStatus;

public class DownloadException extends BaseException {
    public DownloadException(String message) {
        super(message, "DOWNLOAD_ERROR", HttpStatus.BAD_REQUEST);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, "DOWNLOAD_ERROR", cause);
    }
}
