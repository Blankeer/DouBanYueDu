package com.douban.book.reader.manager.exception;

import com.douban.book.reader.R;
import com.douban.book.reader.exception.HumanReadable;
import com.douban.book.reader.util.Res;

public class NoSuchDataException extends DataLoadException implements HumanReadable {
    public NoSuchDataException(Throwable cause) {
        super(cause);
    }

    public NoSuchDataException(String detailMessage) {
        super(detailMessage);
    }

    public NoSuchDataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        return Res.getString(R.string.error_rest_not_found);
    }
}
