package com.douban.book.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.OpenIdAuthenticatedEvent;
import com.douban.book.reader.manager.SessionManager;
import com.douban.book.reader.network.client.JsonClient;
import com.douban.book.reader.network.param.QueryString;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.AnalysisUtils;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.WXUtils;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.open.SocialConstants;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.json.JSONObject;

@EActivity
public class WeixinAuthActivity extends BaseBlankActivity implements IWXAPIEventHandler {
    private static final int WX_RESP_CODE_AUTH_DENIED = 2;
    private static final int WX_RESP_CODE_OK = 0;
    private static final int WX_RESP_CODE_UNKNOWN = 3;
    private static final int WX_RESP_CODE_USER_CANCEL = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.act_wx_entry);
        WXUtils.handleIntent(getIntent(), this);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        WXUtils.handleIntent(getIntent(), this);
    }

    public void onReq(BaseReq baseReq) {
        WelcomeActivity_.intent((Context) this).start();
        finish();
    }

    public void onResp(BaseResp resp) {
        if (resp instanceof Resp) {
            handleAuthResp((Resp) resp);
        } else {
            handleShareResp(resp);
        }
        finish();
    }

    private void handleAuthResp(Resp resp) {
        if (resp.errCode == 0) {
            getOpenId(resp.code);
        } else {
            ToastUtils.showToast((int) R.string.error_login_not_completed);
        }
    }

    @Background
    void getOpenId(String code) {
        try {
            JSONObject json = (JSONObject) new JsonClient("https://api.weixin.qq.com/sns/oauth2/access_token").get(((QueryString) ((QueryString) ((QueryString) RequestParam.queryString().append(SocialConstants.PARAM_APP_ID, SessionManager.OPENID_APPID_WEIXIN)).append("secret", Constants.WX_APP_KEY)).append(SelectCountryActivity.EXTRA_COUNTRY_CODE, code)).append(WBConstants.AUTH_PARAMS_GRANT_TYPE, "authorization_code"));
            EventBusUtils.post(new OpenIdAuthenticatedEvent(Header.ARRAY_BYTE_ALL_EQUAL, json.optString(SocialConstants.PARAM_OPEN_ID), json.optString(ShareRequestParam.REQ_PARAM_TOKEN)));
        } catch (Throwable e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.error_login_fail));
        }
    }

    private void handleShareResp(BaseResp resp) {
        int toastRes;
        int respCode;
        switch (resp.errCode) {
            case ErrCode.ERR_AUTH_DENIED /*-4*/:
                toastRes = R.string.toast_weixin_share_result_denied;
                respCode = WX_RESP_CODE_AUTH_DENIED;
                break;
            case ErrCode.ERR_USER_CANCEL /*-2*/:
                toastRes = R.string.toast_weixin_share_result_cancel;
                respCode = WX_RESP_CODE_USER_CANCEL;
                break;
            case WX_RESP_CODE_OK /*0*/:
                toastRes = R.string.toast_weixin_share_result_ok;
                respCode = WX_RESP_CODE_OK;
                break;
            default:
                toastRes = R.string.toast_weixin_share_result_unknown;
                respCode = WX_RESP_CODE_UNKNOWN;
                break;
        }
        ToastUtils.showToast(toastRes);
        AnalysisUtils.sendGetShareRespFromWXEvent(resp.transaction, (long) respCode);
    }
}
