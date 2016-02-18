package com.douban.book.reader.exception;

import com.douban.book.reader.R;
import com.douban.book.reader.util.Res;

public class WeixinShareException extends Exception implements HumanReadable {
    public WeixinShareException(Throwable cause) {
        super(cause);
    }

    public WeixinShareException(String detailMessage) {
        super(detailMessage);
    }

    public WeixinShareException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        return Res.getString(R.string.toast_share_error);
    }
}
