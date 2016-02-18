package com.douban.book.reader.alipay;

import android.app.Activity;
import com.alipay.sdk.app.PayTask;
import com.douban.book.reader.R;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class MobileSecurePayer {
    private static final String TAG;

    static {
        TAG = MobileSecurePayer.class.getSimpleName();
    }

    public String pay(String orderInfo, Activity activity) throws AlipayException {
        try {
            Logger.d(TAG, "orderInfo: %s", orderInfo);
            Logger.d(TAG, "result: %s", new PayTask(activity).pay(orderInfo));
            Map<String, String> map = parseResult(result);
            int resultStatus = StringUtils.toInt((CharSequence) map.get("resultStatus"));
            if (resultStatus == 9000) {
                return (String) map.get("result");
            }
            String memo = (String) map.get("memo");
            if (resultStatus == 6001) {
                memo = Res.getString(R.string.purchase_error_cancelled);
            } else if (StringUtils.isEmpty(memo)) {
                memo = Res.getString(R.string.purchase_error_general_failure);
            } else {
                memo = Res.getString(R.string.purchase_error_failed_info_from_sdk, Integer.valueOf(resultStatus), memo);
            }
            throw new AlipayException(memo);
        } catch (Exception e) {
            if (e instanceof AlipayException) {
                throw e;
            }
            throw new AlipayException(Res.getString(R.string.purchase_error_general_failure), e);
        }
    }

    private Map<String, String> parseResult(String result) {
        Map<String, String> map = new HashMap();
        for (String section : result.split(";")) {
            int firstEqual = section.indexOf(61);
            map.put(section.substring(0, firstEqual), section.substring(firstEqual + 2, section.length() - 1));
        }
        return map;
    }
}
