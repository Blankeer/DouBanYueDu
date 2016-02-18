package com.sina.weibo.sdk.auth;

import android.os.Bundle;
import android.text.TextUtils;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class Oauth2AccessToken {
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_PHONE_NUM = "phone_num";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_UID = "uid";
    private String mAccessToken;
    private long mExpiresTime;
    private String mPhoneNum;
    private String mRefreshToken;
    private String mUid;

    public Oauth2AccessToken() {
        this.mUid = Table.STRING_DEFAULT_VALUE;
        this.mAccessToken = Table.STRING_DEFAULT_VALUE;
        this.mRefreshToken = Table.STRING_DEFAULT_VALUE;
        this.mExpiresTime = 0;
        this.mPhoneNum = Table.STRING_DEFAULT_VALUE;
    }

    @Deprecated
    public Oauth2AccessToken(String responseText) {
        this.mUid = Table.STRING_DEFAULT_VALUE;
        this.mAccessToken = Table.STRING_DEFAULT_VALUE;
        this.mRefreshToken = Table.STRING_DEFAULT_VALUE;
        this.mExpiresTime = 0;
        this.mPhoneNum = Table.STRING_DEFAULT_VALUE;
        if (responseText != null && responseText.indexOf("{") >= 0) {
            try {
                JSONObject json = new JSONObject(responseText);
                setUid(json.optString(KEY_UID));
                setToken(json.optString(KEY_ACCESS_TOKEN));
                setExpiresIn(json.optString(KEY_EXPIRES_IN));
                setRefreshToken(json.optString(KEY_REFRESH_TOKEN));
                setPhoneNum(json.optString(KEY_PHONE_NUM));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Oauth2AccessToken(String accessToken, String expiresIn) {
        this.mUid = Table.STRING_DEFAULT_VALUE;
        this.mAccessToken = Table.STRING_DEFAULT_VALUE;
        this.mRefreshToken = Table.STRING_DEFAULT_VALUE;
        this.mExpiresTime = 0;
        this.mPhoneNum = Table.STRING_DEFAULT_VALUE;
        this.mAccessToken = accessToken;
        this.mExpiresTime = System.currentTimeMillis();
        if (expiresIn != null) {
            this.mExpiresTime += Long.parseLong(expiresIn) * 1000;
        }
    }

    public static Oauth2AccessToken parseAccessToken(String responseJsonText) {
        if (!TextUtils.isEmpty(responseJsonText) && responseJsonText.indexOf("{") >= 0) {
            try {
                JSONObject json = new JSONObject(responseJsonText);
                Oauth2AccessToken token = new Oauth2AccessToken();
                token.setUid(json.optString(KEY_UID));
                token.setToken(json.optString(KEY_ACCESS_TOKEN));
                token.setExpiresIn(json.optString(KEY_EXPIRES_IN));
                token.setRefreshToken(json.optString(KEY_REFRESH_TOKEN));
                token.setPhoneNum(json.optString(KEY_PHONE_NUM));
                return token;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Oauth2AccessToken parseAccessToken(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        Oauth2AccessToken accessToken = new Oauth2AccessToken();
        accessToken.setUid(getString(bundle, KEY_UID, Table.STRING_DEFAULT_VALUE));
        accessToken.setToken(getString(bundle, KEY_ACCESS_TOKEN, Table.STRING_DEFAULT_VALUE));
        accessToken.setExpiresIn(getString(bundle, KEY_EXPIRES_IN, Table.STRING_DEFAULT_VALUE));
        accessToken.setRefreshToken(getString(bundle, KEY_REFRESH_TOKEN, Table.STRING_DEFAULT_VALUE));
        accessToken.setPhoneNum(getString(bundle, KEY_PHONE_NUM, Table.STRING_DEFAULT_VALUE));
        return accessToken;
    }

    public boolean isSessionValid() {
        return !TextUtils.isEmpty(this.mAccessToken);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_UID, this.mUid);
        bundle.putString(KEY_ACCESS_TOKEN, this.mAccessToken);
        bundle.putString(KEY_REFRESH_TOKEN, this.mRefreshToken);
        bundle.putString(KEY_EXPIRES_IN, Long.toString(this.mExpiresTime));
        bundle.putString(KEY_PHONE_NUM, this.mPhoneNum);
        return bundle;
    }

    public String toString() {
        return "uid: " + this.mUid + ", " + KEY_ACCESS_TOKEN + ": " + this.mAccessToken + ", " + KEY_REFRESH_TOKEN + ": " + this.mRefreshToken + ", " + KEY_PHONE_NUM + ": " + this.mPhoneNum + ", " + KEY_EXPIRES_IN + ": " + Long.toString(this.mExpiresTime);
    }

    public String getUid() {
        return this.mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    public String getToken() {
        return this.mAccessToken;
    }

    public void setToken(String mToken) {
        this.mAccessToken = mToken;
    }

    public String getRefreshToken() {
        return this.mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    public long getExpiresTime() {
        return this.mExpiresTime;
    }

    public void setExpiresTime(long mExpiresTime) {
        this.mExpiresTime = mExpiresTime;
    }

    public void setExpiresIn(String expiresIn) {
        if (!TextUtils.isEmpty(expiresIn) && !expiresIn.equals(Constants.VIA_RESULT_SUCCESS)) {
            setExpiresTime(System.currentTimeMillis() + (Long.parseLong(expiresIn) * 1000));
        }
    }

    private static String getString(Bundle bundle, String key, String defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        String value = bundle.getString(key);
        return value != null ? value : defaultValue;
    }

    public String getPhoneNum() {
        return this.mPhoneNum;
    }

    private void setPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }
}
