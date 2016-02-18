package com.sina.weibo.sdk.call;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.sina.weibo.sdk.constant.WBPageConstants.ExceptionMsg;
import java.util.HashMap;

class CommonUtils {
    CommonUtils() {
    }

    public static String buildUriQuery(HashMap<String, String> paramsMap) {
        StringBuilder queryBuilder = new StringBuilder();
        for (String key : paramsMap.keySet()) {
            String value = (String) paramsMap.get(key);
            if (value != null) {
                queryBuilder.append("&").append(key).append(SimpleComparison.EQUAL_TO_OPERATION).append(value);
            }
        }
        return queryBuilder.toString().replaceFirst("&", "?");
    }

    public static void openWeiboActivity(Context context, String action, String uri, String packageName) throws WeiboNotInstalledException {
        Intent intent;
        if (packageName != null) {
            try {
                intent = new Intent();
                intent.setAction(action);
                intent.setData(Uri.parse(uri));
                intent.setPackage(packageName);
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException e) {
                if (packageName != null) {
                    try {
                        intent = new Intent();
                        intent.setAction(action);
                        intent.setData(Uri.parse(uri));
                        context.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException e2) {
                        throw new WeiboNotInstalledException(ExceptionMsg.WEIBO_NOT_INSTALLED);
                    }
                }
                throw new WeiboNotInstalledException(ExceptionMsg.WEIBO_NOT_INSTALLED);
            }
        }
        intent = new Intent();
        intent.setAction(action);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

    public static void openWeiboActivity(Context context, String action, String uri) throws WeiboNotInstalledException {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new WeiboNotInstalledException(ExceptionMsg.WEIBO_NOT_INSTALLED);
        }
    }
}
