package com.douban.book.reader.network.exception;

import com.douban.book.reader.R;
import com.douban.book.reader.util.Res;

public class RestParseException extends RestException {
    public RestParseException(Throwable cause) {
        super(cause);
    }

    public RestParseException(String detailMessage) {
        super(detailMessage);
    }

    public RestParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        return Res.getString(R.string.error_rest_parse_error);
    }
}
