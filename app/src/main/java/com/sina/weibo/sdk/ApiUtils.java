package com.sina.weibo.sdk;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.MD5;

public class ApiUtils {
    public static final int BUILD_INT = 10350;
    public static final int BUILD_INT_440 = 10355;
    public static final int BUILD_INT_VER_2_2 = 10351;
    public static final int BUILD_INT_VER_2_3 = 10352;
    public static final int BUILD_INT_VER_2_5 = 10353;
    private static final String TAG;

    static {
        TAG = ApiUtils.class.getName();
    }

    public static boolean validateWeiboSign(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            return containSign(context.getPackageManager().getPackageInfo(pkgName, 64).signatures, WBConstants.WEIBO_SIGN);
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private static boolean containSign(Signature[] signatures, String destSign) {
        if (signatures == null || destSign == null) {
            return false;
        }
        for (Signature signature : signatures) {
            if (destSign.equals(MD5.hexdigest(signature.toByteArray()))) {
                LogUtil.d(TAG, "check pass");
                return true;
            }
        }
        return false;
    }
}
