package com.douban.book.reader.alipay;

import com.douban.book.reader.exception.HumanReadable;

public class AlipayException extends Exception implements HumanReadable {
    public AlipayException(Throwable cause) {
        super(cause);
    }

    public AlipayException(String detailMessage) {
        super(detailMessage);
    }

    public AlipayException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        return getMessage();
    }
}
