package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.OpenIdAuthenticatedEvent;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONObject;

public class QQAuthActivity extends BaseBlankActivity {
    private static final String TAG;
    private static final String TENCENT_APP_ID = "100782785";
    private Tencent mTencent;

    static {
        TAG = QQAuthActivity.class.getSimpleName();
    }

    public static void startAuth(PageOpenHelper helper) {
        helper.open(new Intent(App.get(), QQAuthActivity.class));
    }

    public static boolean isAuthenticated() {
        String openId = Pref.ofApp().getString(Key.APP_QQ_OPEN_ID);
        String accessToken = Pref.ofApp().getString(Key.APP_QQ_ACCESS_TOKEN);
        int expiredAt = Pref.ofApp().getInt(Key.APP_QQ_ACCESS_TOKEN_EXPIRES_AT, 0);
        if (!StringUtils.isNotEmpty(openId, accessToken) || expiredAt <= Utils.currentTime()) {
            return false;
        }
        return true;
    }

    public static void clearToken() {
        Pref.ofApp().remove(Key.APP_QQ_ACCESS_TOKEN);
        Pref.ofApp().remove(Key.APP_QQ_OPEN_ID);
        Pref.ofApp().remove(Key.APP_QQ_ACCESS_TOKEN_EXPIRES_AT);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTencent = Tencent.createInstance(TENCENT_APP_ID, getApplicationContext());
        this.mTencent.login((Activity) this, "get_user_info", new IUiListener() {
            public void onComplete(Object result) {
                JSONObject resultJson = (JSONObject) result;
                Logger.d(QQAuthActivity.TAG, "onComplete() %s", resultJson.toString());
                String openId = resultJson.optString(SocialConstants.PARAM_OPEN_ID);
                String accessToken = resultJson.optString(ShareRequestParam.REQ_PARAM_TOKEN);
                String expiresIn = resultJson.optString(Constants.PARAM_EXPIRES_IN);
                Pref.ofApp().set(Key.APP_QQ_OPEN_ID, openId);
                Pref.ofApp().set(Key.APP_QQ_ACCESS_TOKEN, accessToken);
                Pref.ofApp().set(Key.APP_QQ_ACCESS_TOKEN_EXPIRES_AT, Integer.valueOf(Utils.currentTime() + StringUtils.toInt(expiresIn)));
                EventBusUtils.post(ArkEvent.QQ_AUTHENTICATED);
                EventBusUtils.post(new OpenIdAuthenticatedEvent(Header.DOUBLE_0, openId, accessToken));
                QQAuthActivity.this.setResult(-1);
                QQAuthActivity.this.finish();
            }

            public void onError(UiError uiError) {
                Logger.e(QQAuthActivity.TAG, "onError() %s %s %s", Integer.valueOf(uiError.errorCode), uiError.errorDetail, uiError.errorMessage);
                QQAuthActivity.this.setResult(0);
                QQAuthActivity.this.finish();
            }

            public void onCancel() {
                QQAuthActivity.this.setResult(0);
                QQAuthActivity.this.finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mTencent.onActivityResult(requestCode, resultCode, data);
    }
}
