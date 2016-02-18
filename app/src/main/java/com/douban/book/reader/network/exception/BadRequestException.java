package com.douban.book.reader.network.exception;

import android.support.v4.media.TransportMediator;
import com.douban.book.reader.R;
import com.douban.book.reader.exception.HumanReadable;
import com.douban.book.reader.util.Res;

public class BadRequestException extends RestServerException implements HumanReadable {
    private int mErrorCode;
    private String mMessage;

    public BadRequestException(int errorCode, String message, Throwable cause) {
        super(cause);
        this.mErrorCode = errorCode;
        this.mMessage = message;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public CharSequence getHumanReadableMessage() {
        switch (this.mErrorCode) {
            case Header.ARRAY_BOOLEAN /*111*/:
            case Header.ARRAY_SHORT /*112*/:
                return Res.getString(R.string.toast_api_error_limit_exceed);
            case Header.ARRAY_LONG_BYTE /*120*/:
                return Res.getString(R.string.error_login_failed_username_password_mismatch);
            case TransportMediator.FLAG_KEY_MEDIA_NEXT /*128*/:
                return Res.getString(R.string.error_user_locked);
            case 1003:
            case 9003:
                return Res.getString(R.string.error_image_too_large);
            case 1004:
            case 9021:
                return Res.getString(R.string.error_has_ban_word);
            case 1008:
            case 1009:
            case 9008:
            case 9009:
                return Res.getString(R.string.error_image_format_unknown);
            case 9030:
                return Res.getString(R.string.error_nickname_too_long);
            default:
                if (this.mErrorCode <= 0) {
                    return Res.getString(R.string.error_rest_bad_request);
                }
                return String.format("%s (err %d)", new Object[]{this.mMessage, Integer.valueOf(this.mErrorCode)});
        }
    }
}
