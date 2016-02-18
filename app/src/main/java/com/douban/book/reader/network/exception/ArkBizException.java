package com.douban.book.reader.network.exception;

import com.douban.book.reader.exception.HumanReadable;
import com.douban.book.reader.util.StringUtils;

public class ArkBizException extends RestServerException implements HumanReadable {
    private final String mHumanReadableMessage;

    public ArkBizException(String humanReadableMessage, Throwable cause) {
        super(cause);
        this.mHumanReadableMessage = humanReadableMessage;
    }

    public CharSequence getHumanReadableMessage() {
        if (StringUtils.isNotEmpty(this.mHumanReadableMessage)) {
            return this.mHumanReadableMessage;
        }
        return super.getHumanReadableMessage();
    }
}
