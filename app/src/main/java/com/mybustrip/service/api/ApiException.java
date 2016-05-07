package com.mybustrip.service.api;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class ApiException extends RuntimeException {

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public ApiException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
