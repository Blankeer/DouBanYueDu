package io.fabric.sdk.android.services.common;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.helper.AppUri;
import com.igexin.download.Downloads;

public class ResponseParser {
    public static final int ResponseActionDiscard = 0;
    public static final int ResponseActionRetry = 1;

    public static int parse(int statusCode) {
        if (statusCode >= Downloads.STATUS_SUCCESS && statusCode <= 299) {
            return ResponseActionDiscard;
        }
        if (statusCode >= AppUri.READER && statusCode <= 399) {
            return ResponseActionRetry;
        }
        if (statusCode >= Downloads.STATUS_BAD_REQUEST && statusCode <= 499) {
            return ResponseActionDiscard;
        }
        if (statusCode >= StatConstant.DEFAULT_MAX_EVENT_COUNT) {
            return ResponseActionRetry;
        }
        return ResponseActionRetry;
    }
}
