package com.douban.book.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.WeiboAuthActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.OpenIdAuthenticatedEvent;
import com.douban.book.reader.event.WeiboUserNameUpdatedEvent;
import com.douban.book.reader.network.client.JsonClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.QueryString;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBPageConstants.ParamKey;
import com.sina.weibo.sdk.exception.WeiboException;
import io.realm.internal.Table;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONObject;

@EActivity
public class WeiboAuthActivity extends BaseBlankActivity {
    private static final String TAG;
    @Extra
    Intent intentToForwardAfterAuth;
    private SsoHandler mSsoHandler;

    static {
        TAG = WeiboAuthActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSSOAuth();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mSsoHandler != null) {
            this.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public static boolean isAuthenticated() {
        String userId = Pref.ofApp().getString(Key.APP_WEIBO_USER_ID);
        String accessToken = Pref.ofApp().getString(Key.APP_WEIBO_ACCESS_TOKEN);
        int expiredAt = Pref.ofApp().getInt(Key.APP_WEIBO_ACCESS_TOKEN_EXPIRES_AT, 0);
        if (!StringUtils.isNotEmpty(userId, accessToken) || expiredAt <= Utils.currentTime()) {
            return false;
        }
        return true;
    }

    public static void clearToken() {
        Pref.ofApp().remove(Key.APP_WEIBO_USER_NAME);
        Pref.ofApp().remove(Key.APP_WEIBO_USER_ID);
        Pref.ofApp().remove(Key.APP_WEIBO_ACCESS_TOKEN);
        Pref.ofApp().remove(Key.APP_WEIBO_ACCESS_TOKEN_EXPIRES_AT);
    }

    public static void startAuth(PageOpenHelper helper, Intent intentToForwardAfterAuth) {
        helper.open(((IntentBuilder_) WeiboAuthActivity_.intent(App.get()).flags(268435456)).intentToForwardAfterAuth(intentToForwardAfterAuth).get());
    }

    public static void startAuth(PageOpenHelper helper) {
        startAuth(helper, null);
    }

    public static void retrieveUserName() {
        BackgroundExecutor.execute(new Runnable() {
            public void run() {
                try {
                    if (StringUtils.isNotEmpty(((JSONObject) new JsonClient("https://api.weibo.com/2/users/show.json").get(((QueryString) ((QueryString) RequestParam.queryString().append(ShareRequestParam.REQ_PARAM_TOKEN, Pref.ofApp().getString(Key.APP_WEIBO_ACCESS_TOKEN))).append(ShareRequestParam.REQ_PARAM_SOURCE, Constants.WEIBO_APP_KEY)).append(ParamKey.UID, Pref.ofApp().getString(Key.APP_WEIBO_USER_ID)))).optString("screen_name"))) {
                        Pref.ofApp().set(Key.APP_WEIBO_USER_NAME, ((JSONObject) new JsonClient("https://api.weibo.com/2/users/show.json").get(((QueryString) ((QueryString) RequestParam.queryString().append(ShareRequestParam.REQ_PARAM_TOKEN, Pref.ofApp().getString(Key.APP_WEIBO_ACCESS_TOKEN))).append(ShareRequestParam.REQ_PARAM_SOURCE, Constants.WEIBO_APP_KEY)).append(ParamKey.UID, Pref.ofApp().getString(Key.APP_WEIBO_USER_ID)))).optString("screen_name"));
                        EventBusUtils.post(new WeiboUserNameUpdatedEvent());
                    }
                } catch (RestException e) {
                    Logger.e(WeiboAuthActivity.TAG, e);
                }
            }
        });
    }

    private void startSSOAuth() {
        this.mSsoHandler = new SsoHandler(this, new AuthInfo(this, Constants.WEIBO_APP_KEY, "https://api.weibo.com/oauth2/default.html", Table.STRING_DEFAULT_VALUE));
        this.mSsoHandler.authorize(new WeiboAuthListener() {
            public void onComplete(Bundle bundle) {
                String userId = bundle.getString(ParamKey.UID);
                String userName = bundle.getString("userName");
                String accessToken = bundle.getString(ShareRequestParam.REQ_PARAM_TOKEN);
                String expiresIn = bundle.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                Pref.ofApp().set(Key.APP_WEIBO_USER_ID, userId);
                Pref.ofApp().set(Key.APP_WEIBO_USER_NAME, userName);
                Pref.ofApp().set(Key.APP_WEIBO_ACCESS_TOKEN, accessToken);
                Pref.ofApp().set(Key.APP_WEIBO_ACCESS_TOKEN_EXPIRES_AT, Integer.valueOf(Utils.currentTime() + StringUtils.toInt(expiresIn)));
                if (StringUtils.isEmpty(userName)) {
                    WeiboAuthActivity.retrieveUserName();
                }
                EventBusUtils.post(ArkEvent.WEIBO_AUTHENTICATED);
                EventBusUtils.post(new OpenIdAuthenticatedEvent(Header.DOUBLE_1, userId, accessToken));
                if (WeiboAuthActivity.this.intentToForwardAfterAuth != null) {
                    PageOpenHelper.from(WeiboAuthActivity.this).open(WeiboAuthActivity.this.intentToForwardAfterAuth);
                }
                WeiboAuthActivity.this.setResult(-1);
                WeiboAuthActivity.this.finish();
            }

            public void onWeiboException(WeiboException e) {
                WeiboAuthActivity.this.showToast(Res.getString(R.string.toast_third_party_login_failed, e.getLocalizedMessage()));
                WeiboAuthActivity.this.setResult(0);
                WeiboAuthActivity.this.finish();
            }

            public void onCancel() {
                WeiboAuthActivity.this.setResult(0);
                WeiboAuthActivity.this.finish();
            }
        });
    }
}
