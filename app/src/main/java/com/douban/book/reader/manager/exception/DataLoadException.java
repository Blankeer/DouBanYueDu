package com.douban.book.reader.manager.exception;

import java.io.IOException;

public class DataLoadException extends IOException {
    public DataLoadException(Throwable cause) {
        super(cause);
    }

    public DataLoadException(String detailMessage) {
        super(detailMessage);
    }

    public DataLoadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
