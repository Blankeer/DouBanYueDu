package com.douban.book.reader.network.exception;

import java.net.HttpURLConnection;

public class CdnConnException extends RestServerException {
    public CdnConnException(HttpURLConnection conn) {
        super(conn);
    }

    public CdnConnException(Throwable cause) {
        super(cause);
    }
}
