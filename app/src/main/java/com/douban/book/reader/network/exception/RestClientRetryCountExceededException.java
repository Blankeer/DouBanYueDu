package com.douban.book.reader.network.exception;

public class RestClientRetryCountExceededException extends Exception {
    public RestClientRetryCountExceededException(Throwable cause) {
        super(cause);
    }

    public RestClientRetryCountExceededException(String detailMessage) {
        super(detailMessage);
    }

    public RestClientRetryCountExceededException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
