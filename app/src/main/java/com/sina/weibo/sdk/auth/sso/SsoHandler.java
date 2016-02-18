package com.sina.weibo.sdk.auth.sso;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.douban.amonsul.StatConstant;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.sina.sso.RemoteSSO;
import com.sina.sso.RemoteSSO.Stub;
import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.cmd.WbAppActivator;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboDialogException;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.register.mobile.MobileRegisterActivity;
import com.sina.weibo.sdk.utils.AidTask;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.SecurityHelper;
import com.sina.weibo.sdk.utils.Utility;

public class SsoHandler {
    public static final String AUTH_FAILED_MSG = "auth failed!!!!!";
    public static final String AUTH_FAILED_NOT_INSTALL_MSG = "not install weibo client!!!!!";
    private static final String DEFAULT_WEIBO_REMOTE_SSO_SERVICE_NAME = "com.sina.weibo.remotessoservice";
    private static final int REQUEST_CODE_MOBILE_REGISTER = 40000;
    private static final int REQUEST_CODE_SSO_AUTH = 32973;
    private static final String TAG = "Weibo_SSO_login";
    private Activity mAuthActivity;
    private AuthInfo mAuthInfo;
    private WeiboAuthListener mAuthListener;
    private ServiceConnection mConnection;
    private int mSSOAuthRequestCode;
    private WebAuthHandler mWebAuthHandler;
    private WeiboInfo mWeiboInfo;

    private enum AuthType {
        ALL,
        SsoOnly,
        WebOnly
    }

    public SsoHandler(Activity activity, AuthInfo weiboAuthInfo) {
        this.mConnection = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName name) {
                SsoHandler.this.mWebAuthHandler.anthorize(SsoHandler.this.mAuthListener);
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                RemoteSSO remoteSSOservice = Stub.asInterface(service);
                try {
                    String ssoPackageName = remoteSSOservice.getPackageName();
                    String ssoActivityName = remoteSSOservice.getActivityName();
                    SsoHandler.this.mAuthActivity.getApplicationContext().unbindService(SsoHandler.this.mConnection);
                    if (!SsoHandler.this.startSingleSignOn(ssoPackageName, ssoActivityName)) {
                        SsoHandler.this.mWebAuthHandler.anthorize(SsoHandler.this.mAuthListener);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        this.mAuthActivity = activity;
        this.mAuthInfo = weiboAuthInfo;
        this.mWebAuthHandler = new WebAuthHandler(activity, weiboAuthInfo);
        this.mWeiboInfo = WeiboAppManager.getInstance(activity).getWeiboInfo();
        AidTask.getInstance(this.mAuthActivity).aidTaskInit(weiboAuthInfo.getAppKey());
    }

    public void authorize(WeiboAuthListener listener) {
        authorize(REQUEST_CODE_SSO_AUTH, listener, AuthType.ALL);
        WbAppActivator.getInstance(this.mAuthActivity, this.mAuthInfo.getAppKey()).activateApp();
    }

    public void authorizeClientSso(WeiboAuthListener listener) {
        authorize(REQUEST_CODE_SSO_AUTH, listener, AuthType.SsoOnly);
        WbAppActivator.getInstance(this.mAuthActivity, this.mAuthInfo.getAppKey()).activateApp();
    }

    public void authorizeWeb(WeiboAuthListener listener) {
        authorize(REQUEST_CODE_SSO_AUTH, listener, AuthType.WebOnly);
        WbAppActivator.getInstance(this.mAuthActivity, this.mAuthInfo.getAppKey()).activateApp();
    }

    private void authorize(int requestCode, WeiboAuthListener listener, AuthType authType) {
        this.mSSOAuthRequestCode = requestCode;
        this.mAuthListener = listener;
        boolean onlyClientSso = false;
        if (authType == AuthType.SsoOnly) {
            onlyClientSso = true;
        }
        if (authType == AuthType.WebOnly) {
            if (listener != null) {
                this.mWebAuthHandler.anthorize(listener);
            }
        } else if (!bindRemoteSSOService(this.mAuthActivity.getApplicationContext())) {
            if (!onlyClientSso) {
                this.mWebAuthHandler.anthorize(this.mAuthListener);
            } else if (this.mAuthListener != null) {
                this.mAuthListener.onWeiboException(new WeiboException(AUTH_FAILED_NOT_INSTALL_MSG));
            }
        }
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        String error;
        Bundle bundle;
        Oauth2AccessToken accessToken;
        if (requestCode == this.mSSOAuthRequestCode) {
            if (resultCode == -1) {
                if (SecurityHelper.checkResponseAppLegal(this.mAuthActivity, this.mWeiboInfo, data)) {
                    error = data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR);
                    if (error == null) {
                        error = data.getStringExtra("error_type");
                    }
                    if (error == null) {
                        bundle = data.getExtras();
                        accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        if (accessToken == null || !accessToken.isSessionValid()) {
                            LogUtil.d(TAG, "Failed to receive access token by SSO");
                            this.mWebAuthHandler.anthorize(this.mAuthListener);
                            return;
                        }
                        LogUtil.d(TAG, "Login Success! " + accessToken.toString());
                        this.mAuthListener.onComplete(bundle);
                    } else if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
                        LogUtil.d(TAG, "Login canceled by user.");
                        this.mAuthListener.onCancel();
                    } else {
                        String description = data.getStringExtra("error_description");
                        if (description != null) {
                            error = new StringBuilder(String.valueOf(error)).append(":").append(description).toString();
                        }
                        LogUtil.d(TAG, "Login failed: " + error);
                        this.mAuthListener.onWeiboException(new WeiboDialogException(error, resultCode, description));
                    }
                }
            } else if (resultCode != 0) {
            } else {
                if (data != null) {
                    LogUtil.d(TAG, "Login failed: " + data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR));
                    this.mAuthListener.onWeiboException(new WeiboDialogException(data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR), data.getIntExtra("error_code", -1), data.getStringExtra("failing_url")));
                    return;
                }
                LogUtil.d(TAG, "Login canceled by user.");
                this.mAuthListener.onCancel();
            }
        } else if (requestCode != REQUEST_CODE_MOBILE_REGISTER) {
        } else {
            if (resultCode == -1) {
                bundle = data.getExtras();
                accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                if (accessToken != null && accessToken.isSessionValid()) {
                    LogUtil.d(TAG, "Login Success! " + accessToken.toString());
                    this.mAuthListener.onComplete(bundle);
                }
            } else if (resultCode != 0) {
            } else {
                if (data != null) {
                    LogUtil.d(TAG, "Login failed: " + data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR));
                    error = data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR);
                    if (error == null) {
                        error = data.getStringExtra("error_type");
                    }
                    if (error != null) {
                        this.mAuthListener.onWeiboException(new WeiboDialogException(data.getStringExtra(StatConstant.STAT_EVENT_ID_ERROR), data.getIntExtra("error_code", -1), data.getStringExtra("error_description")));
                        return;
                    }
                    return;
                }
                LogUtil.d(TAG, "Login canceled by user.");
                this.mAuthListener.onCancel();
            }
        }
    }

    public static ComponentName isServiceExisted(Context context, String packageName) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService("activity")).getRunningServices(AdvancedShareActionProvider.WEIGHT_MAX)) {
            ComponentName serviceName = runningServiceInfo.service;
            if (serviceName.getPackageName().equals(packageName) && serviceName.getClassName().equals(new StringBuilder(String.valueOf(packageName)).append(".business.RemoteSSOService").toString())) {
                return serviceName;
            }
        }
        return null;
    }

    private boolean bindRemoteSSOService(Context context) {
        if (!isWeiboAppInstalled()) {
            return false;
        }
        String pkgName = this.mWeiboInfo.getPackageName();
        Intent intent = new Intent(DEFAULT_WEIBO_REMOTE_SSO_SERVICE_NAME);
        intent.setPackage(pkgName);
        return context.bindService(intent, this.mConnection, 1);
    }

    private boolean startSingleSignOn(String ssoPackageName, String ssoActivityName) {
        boolean bSucceed = true;
        Intent intent = new Intent();
        intent.setClassName(ssoPackageName, ssoActivityName);
        intent.putExtras(this.mWebAuthHandler.getAuthInfo().getAuthBundle());
        intent.putExtra(WBConstants.COMMAND_TYPE_KEY, 3);
        intent.putExtra(WBConstants.TRAN, String.valueOf(System.currentTimeMillis()));
        intent.putExtra(ShareRequestParam.REQ_PARAM_AID, Utility.getAid(this.mAuthActivity, this.mAuthInfo.getAppKey()));
        if (!SecurityHelper.validateAppSignatureForIntent(this.mAuthActivity, intent)) {
            return false;
        }
        String aid = Utility.getAid(this.mAuthActivity, this.mAuthInfo.getAppKey());
        if (!TextUtils.isEmpty(aid)) {
            intent.putExtra(ShareRequestParam.REQ_PARAM_AID, aid);
        }
        try {
            this.mAuthActivity.startActivityForResult(intent, this.mSSOAuthRequestCode);
        } catch (ActivityNotFoundException e) {
            bSucceed = false;
        }
        return bSucceed;
    }

    public boolean isWeiboAppInstalled() {
        return this.mWeiboInfo != null && this.mWeiboInfo.isLegal();
    }

    public void registerOrLoginByMobile(String title, WeiboAuthListener listener) {
        this.mAuthListener = listener;
        Intent intentTemp = new Intent(this.mAuthActivity, MobileRegisterActivity.class);
        Bundle param = this.mAuthInfo.getAuthBundle();
        param.putString(MobileRegisterActivity.REGISTER_TITLE, title);
        intentTemp.putExtras(param);
        this.mAuthActivity.startActivityForResult(intentTemp, REQUEST_CODE_MOBILE_REGISTER);
    }
}
