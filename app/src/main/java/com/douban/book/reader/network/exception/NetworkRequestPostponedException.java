package com.douban.book.reader.network.exception;

import com.douban.book.reader.R;
import com.douban.book.reader.exception.HumanReadable;
import com.douban.book.reader.util.Res;

public class NetworkRequestPostponedException extends RestException implements HumanReadable {
    public NetworkRequestPostponedException(Throwable cause) {
        super(cause);
    }

    public NetworkRequestPostponedException(String detailMessage) {
        super(detailMessage);
    }

    public NetworkRequestPostponedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CharSequence getHumanReadableMessage() {
        return Res.getString(R.string.error_network_request_postponed);
    }
}
