package com.douban.book.reader.network.exception;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.igexin.download.Downloads;
import io.realm.internal.Table;
import java.io.IOException;
import java.net.HttpURLConnection;

public class RestServerException extends RestException {
    private int mResponseCode;
    private String mResponseMessage;

    public RestServerException(Throwable cause) {
        super(cause);
        if (cause instanceof RestServerException) {
            this.mResponseCode = ((RestServerException) cause).getResponseCode();
            this.mResponseMessage = ((RestServerException) cause).getResponseMessage();
        }
    }

    public RestServerException(HttpURLConnection conn) {
        this(conn, getErrorSteamStr(conn), Table.STRING_DEFAULT_VALUE);
    }

    public RestServerException(HttpURLConnection conn, String errorStreamContent, String paramString) {
        super(getExceptionString(conn, errorStreamContent, paramString));
        try {
            this.mResponseCode = conn.getResponseCode();
            this.mResponseMessage = conn.getResponseMessage();
        } catch (IOException e) {
        }
    }

    private static String getErrorSteamStr(HttpURLConnection conn) {
        try {
            return IOUtils.streamToString(conn.getErrorStream());
        } catch (IOException e) {
            return String.format("!!Error while obtaining error stream: e=%s", new Object[]{e});
        }
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponseMessage() {
        return this.mResponseMessage;
    }

    public String formatErrorString() {
        return String.format("%s %s", new Object[]{Integer.valueOf(this.mResponseCode), this.mResponseMessage});
    }

    private static String getExceptionString(HttpURLConnection conn, String errorStringContent, String paramString) {
        try {
            String format;
            String str = "%s %s (%s %s) %s%s";
            r3 = new Object[6];
            if (!StringUtils.isNotEmpty(errorStringContent)) {
                errorStringContent = IOUtils.streamToString(conn.getErrorStream());
            }
            r3[4] = errorStringContent;
            if (StringUtils.isNotEmpty(paramString)) {
                format = String.format(" param=%s", new Object[]{paramString});
            } else {
                format = Table.STRING_DEFAULT_VALUE;
            }
            r3[5] = format;
            return String.format(str, r3);
        } catch (IOException e) {
            return e.toString();
        }
    }

    public CharSequence getHumanReadableMessage() {
        int resId = 0;
        if (getResponseCode() >= StatConstant.DEFAULT_MAX_EVENT_COUNT) {
            resId = R.string.error_rest_internal_server_error;
        } else if (getResponseCode() == 404) {
            resId = R.string.error_rest_not_found;
        } else if (getResponseCode() >= Downloads.STATUS_BAD_REQUEST) {
            resId = R.string.error_rest_bad_request;
        }
        if (resId == 0) {
            return null;
        }
        String errorString = formatErrorString();
        return RichText.buildUpon(resId).appendIf(StringUtils.isNotEmpty(errorString), Character.valueOf(Char.SPACE), Character.valueOf(Char.LEFT_PARENTHESIS), errorString, Character.valueOf(Char.RIGHT_PARENTHESIS));
    }
}
