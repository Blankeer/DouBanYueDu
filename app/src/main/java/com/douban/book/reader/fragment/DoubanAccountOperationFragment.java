package com.douban.book.reader.fragment;

import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.JavascriptInterface;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.entity.Session;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.NewUserRegisteredEvent;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ToastUtils;
import com.tencent.open.SocialConstants;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.json.JSONObject;

@EFragment
public class DoubanAccountOperationFragment extends BaseWebFragment {
    private static final String ACCOUNTS_AUTHORITY = "accounts.douban.com";
    private static final int PAGE_LOGIN = 2;
    private static final int REGISTER_SUCCEED = 1;
    private static final UriMatcher sUriMatcher;
    @FragmentArg
    Action action;
    @FragmentArg
    Intent intentToStartAfterLogin;
    @FragmentArg
    ArkRequest requestToSendAfterLogin;

    public enum Action {
        REGISTER,
        RESET_PASSWORD
    }

    static {
        sUriMatcher = new UriMatcher(-1);
        sUriMatcher.addURI(AppUri.AUTHORITY, "register_succeed", REGISTER_SUCCEED);
        sUriMatcher.addURI(ACCOUNTS_AUTHORITY, "app/login", PAGE_LOGIN);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableJavascript("current_app");
        loadUrl(this.action == Action.RESET_PASSWORD ? resetPasswordUri() : registerUri());
    }

    protected boolean shouldOverrideUrlLoading(String url) {
        Uri uri = Uri.parse(url);
        switch (sUriMatcher.match(uri)) {
            case REGISTER_SUCCEED /*1*/:
                try {
                    JSONObject data = new JSONObject(uri.getQueryParameter("oauth"));
                    EventBusUtils.post(new NewUserRegisteredEvent(data.optString("douban_user_name"), (Session) JsonUtils.fromJsonObj(data, Session.class)));
                    finish();
                    return true;
                } catch (Throwable e) {
                    ToastUtils.showToast(e);
                    break;
                }
            case PAGE_LOGIN /*2*/:
                DoubanLoginFragment_.builder().intentToStartAfterLogin(this.intentToStartAfterLogin).requestToSendAfterLogin(this.requestToSendAfterLogin).build().showAsActivity((Fragment) this);
                finish();
                return true;
        }
        return super.shouldOverrideUrlLoading(url);
    }

    protected void onPageFinished(String url) {
        addClass("ua-android");
        super.onPageFinished(url);
    }

    @JavascriptInterface
    public String getClientVariables() {
        StringBuilder stringBuilder = new StringBuilder();
        Object[] objArr = new Object[REGISTER_SUCCEED];
        objArr[0] = AppUri.PATH_OPEN_URL;
        stringBuilder = new StringBuilder();
        objArr = new Object[REGISTER_SUCCEED];
        objArr[0] = "register_succeed";
        stringBuilder = new StringBuilder();
        objArr = new Object[REGISTER_SUCCEED];
        objArr[0] = "register_succeed";
        return String.valueOf(((JsonRequestParam) RequestParam.json().append("apiKey", Constants.APP_KEY)).append(WorksListFragment_.URI_ARG, ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append("openPage", stringBuilder.append(AppUri.withPath(objArr)).append("?url=").toString())).append("loginSuccess", stringBuilder.append(AppUri.withPath(objArr)).append("?oauth=").toString())).append("registerSuccess", stringBuilder.append(AppUri.withPath(objArr)).append("?oauth=").toString())));
    }

    private static Builder baseUri() {
        return new Builder().scheme(Constants.API_SCHEME).authority(ACCOUNTS_AUTHORITY).appendEncodedPath("app/register").appendQueryParameter(SocialConstants.PARAM_APP_ID, ReaderUri.SCHEME);
    }

    protected static Uri registerUri() {
        return baseUri().build();
    }

    protected static Uri resetPasswordUri() {
        return baseUri().fragment("forget").build();
    }
}
