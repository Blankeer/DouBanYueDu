package com.douban.book.reader.network.exception;

import com.douban.book.reader.R;
import com.douban.book.reader.exception.HumanReadable;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Res;
import java.io.IOException;
import javax.net.ssl.SSLException;

public class RestException extends IOException implements HumanReadable {
    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String detailMessage) {
        super(detailMessage);
    }

    public RestException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        if (ExceptionUtils.isCausedBy(this, InterruptedException.class)) {
            return Res.getString(R.string.error_timeout);
        }
        if (ExceptionUtils.isCausedBy(this, SSLException.class)) {
            return Res.getString(R.string.error_cert);
        }
        if (ExceptionUtils.isCausedBy(this, IOException.class)) {
            return Res.getString(R.string.error_host);
        }
        return null;
    }
}
