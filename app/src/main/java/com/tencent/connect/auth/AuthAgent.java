package com.tencent.connect.auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.amonsul.network.NetWorker;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.a.a;
import com.tencent.connect.common.AssistActivity;
import com.tencent.connect.common.BaseApi;
import com.tencent.connect.common.BaseApi.ApiTask;
import com.tencent.connect.common.BaseApi.TempRequestListener;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import com.tencent.open.a.f;
import com.tencent.open.b.d;
import com.tencent.open.utils.Global;
import com.tencent.open.utils.HttpUtils;
import com.tencent.open.utils.ServerSetting;
import com.tencent.open.utils.SystemUtils;
import com.tencent.open.utils.ThreadManager;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ProGuard */
public class AuthAgent extends BaseApi {
    public static final String SECURE_LIB_FILE_NAME = "libwbsafeedit";
    public static final String SECURE_LIB_NAME = "libwbsafeedit.so";
    private IUiListener a;
    private String b;
    private Activity c;

    /* compiled from: ProGuard */
    /* renamed from: com.tencent.connect.auth.AuthAgent.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ String a;
        final /* synthetic */ IUiListener b;
        final /* synthetic */ AuthAgent c;

        AnonymousClass1(AuthAgent authAgent, String str, IUiListener iUiListener) {
            this.c = authAgent;
            this.a = str;
            this.b = iUiListener;
        }

        public void run() {
            SystemUtils.extractSecureLib(AuthAgent.SECURE_LIB_FILE_NAME, AuthAgent.SECURE_LIB_NAME, 2);
            this.c.c.runOnUiThread(new Runnable() {
                final /* synthetic */ AnonymousClass1 a;

                {
                    this.a = r1;
                }

                public void run() {
                    new AuthDialog(this.a.c.c, SystemUtils.ACTION_LOGIN, this.a.a, this.a.b, this.a.c.mToken).show();
                }
            });
        }
    }

    /* compiled from: ProGuard */
    private class CheckLoginListener implements IUiListener {
        IUiListener a;
        final /* synthetic */ AuthAgent b;

        public CheckLoginListener(AuthAgent authAgent, IUiListener iUiListener) {
            this.b = authAgent;
            this.a = iUiListener;
        }

        public void onComplete(Object obj) {
            if (obj == null) {
                f.e("CheckLoginListener", "response data is null");
                return;
            }
            JSONObject jSONObject = (JSONObject) obj;
            try {
                int i = jSONObject.getInt("ret");
                Object string = i == 0 ? "success" : jSONObject.getString(SocialConstants.PARAM_SEND_MSG);
                if (this.a != null) {
                    this.a.onComplete(new JSONObject().put("ret", i).put(SocialConstants.PARAM_SEND_MSG, string));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                f.e("CheckLoginListener", "response data format error");
            }
        }

        public void onError(UiError uiError) {
            if (this.a != null) {
                this.a.onError(uiError);
            }
        }

        public void onCancel() {
            if (this.a != null) {
                this.a.onCancel();
            }
        }
    }

    /* compiled from: ProGuard */
    private class FeedConfirmListener implements IUiListener {
        IUiListener a;
        final /* synthetic */ AuthAgent b;
        private final String c;
        private final String d;
        private final String e;

        /* compiled from: ProGuard */
        /* renamed from: com.tencent.connect.auth.AuthAgent.FeedConfirmListener.3 */
        class AnonymousClass3 implements OnCancelListener {
            final /* synthetic */ IUiListener a;
            final /* synthetic */ Object b;
            final /* synthetic */ FeedConfirmListener c;

            AnonymousClass3(FeedConfirmListener feedConfirmListener, IUiListener iUiListener, Object obj) {
                this.c = feedConfirmListener;
                this.a = iUiListener;
                this.b = obj;
            }

            public void onCancel(DialogInterface dialogInterface) {
                if (this.a != null) {
                    this.a.onComplete(this.b);
                }
            }
        }

        /* compiled from: ProGuard */
        private abstract class ButtonListener implements OnClickListener {
            Dialog d;
            final /* synthetic */ FeedConfirmListener e;

            ButtonListener(FeedConfirmListener feedConfirmListener, Dialog dialog) {
                this.e = feedConfirmListener;
                this.d = dialog;
            }
        }

        /* compiled from: ProGuard */
        /* renamed from: com.tencent.connect.auth.AuthAgent.FeedConfirmListener.1 */
        class AnonymousClass1 extends ButtonListener {
            final /* synthetic */ IUiListener a;
            final /* synthetic */ Object b;
            final /* synthetic */ FeedConfirmListener c;

            AnonymousClass1(FeedConfirmListener feedConfirmListener, Dialog dialog, IUiListener iUiListener, Object obj) {
                this.c = feedConfirmListener;
                this.a = iUiListener;
                this.b = obj;
                super(feedConfirmListener, dialog);
            }

            public void onClick(View view) {
                this.c.a();
                if (this.d != null && this.d.isShowing()) {
                    this.d.dismiss();
                }
                if (this.a != null) {
                    this.a.onComplete(this.b);
                }
            }
        }

        /* compiled from: ProGuard */
        /* renamed from: com.tencent.connect.auth.AuthAgent.FeedConfirmListener.2 */
        class AnonymousClass2 extends ButtonListener {
            final /* synthetic */ IUiListener a;
            final /* synthetic */ Object b;
            final /* synthetic */ FeedConfirmListener c;

            AnonymousClass2(FeedConfirmListener feedConfirmListener, Dialog dialog, IUiListener iUiListener, Object obj) {
                this.c = feedConfirmListener;
                this.a = iUiListener;
                this.b = obj;
                super(feedConfirmListener, dialog);
            }

            public void onClick(View view) {
                if (this.d != null && this.d.isShowing()) {
                    this.d.dismiss();
                }
                if (this.a != null) {
                    this.a.onComplete(this.b);
                }
            }
        }

        public FeedConfirmListener(AuthAgent authAgent, IUiListener iUiListener) {
            this.b = authAgent;
            this.c = "sendinstall";
            this.d = "installwording";
            this.e = "http://appsupport.qq.com/cgi-bin/qzapps/mapp_addapp.cgi";
            this.a = iUiListener;
        }

        public void onComplete(Object obj) {
            Object obj2 = null;
            if (obj != null) {
                JSONObject jSONObject = (JSONObject) obj;
                if (jSONObject != null) {
                    String string;
                    Object obj3;
                    Object obj4;
                    String str = Table.STRING_DEFAULT_VALUE;
                    try {
                        if (jSONObject.getInt("sendinstall") == 1) {
                            obj2 = 1;
                        }
                        string = jSONObject.getString("installwording");
                        obj3 = obj2;
                    } catch (JSONException e) {
                        obj4 = null;
                        f.d("FeedConfirm", "There is no value for sendinstall.");
                        String str2 = str;
                        obj3 = obj4;
                        string = str2;
                    }
                    obj4 = URLDecoder.decode(string);
                    f.b("TAG", " WORDING = " + obj4 + "xx");
                    if (obj3 != null && !TextUtils.isEmpty(obj4)) {
                        a(obj4, this.a, obj);
                    } else if (this.a != null) {
                        this.a.onComplete(obj);
                    }
                }
            }
        }

        private void a(String str, IUiListener iUiListener, Object obj) {
            PackageInfo packageInfo;
            Drawable drawable = null;
            Dialog dialog = new Dialog(this.b.c);
            dialog.requestWindowFeature(1);
            PackageManager packageManager = this.b.c.getPackageManager();
            try {
                packageInfo = packageManager.getPackageInfo(this.b.c.getPackageName(), 0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                packageInfo = null;
            }
            if (packageInfo != null) {
                drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            }
            OnClickListener anonymousClass1 = new AnonymousClass1(this, dialog, iUiListener, obj);
            OnClickListener anonymousClass2 = new AnonymousClass2(this, dialog, iUiListener, obj);
            Drawable colorDrawable = new ColorDrawable();
            colorDrawable.setAlpha(0);
            dialog.getWindow().setBackgroundDrawable(colorDrawable);
            dialog.setContentView(a(this.b.c, drawable, str, anonymousClass1, anonymousClass2));
            dialog.setOnCancelListener(new AnonymousClass3(this, iUiListener, obj));
            if (this.b.c != null && !this.b.c.isFinishing()) {
                dialog.show();
            }
        }

        private Drawable a(String str, Context context) {
            Drawable createFromStream;
            IOException e;
            try {
                InputStream open = context.getApplicationContext().getAssets().open(str);
                if (open == null) {
                    return null;
                }
                if (str.endsWith(".9.png")) {
                    Bitmap decodeStream;
                    try {
                        decodeStream = BitmapFactory.decodeStream(open);
                    } catch (OutOfMemoryError e2) {
                        e2.printStackTrace();
                        decodeStream = null;
                    }
                    if (decodeStream == null) {
                        return null;
                    }
                    byte[] ninePatchChunk = decodeStream.getNinePatchChunk();
                    NinePatch.isNinePatchChunk(ninePatchChunk);
                    return new NinePatchDrawable(decodeStream, ninePatchChunk, new Rect(), null);
                }
                createFromStream = Drawable.createFromStream(open, str);
                try {
                    open.close();
                    return createFromStream;
                } catch (IOException e3) {
                    e = e3;
                    e.printStackTrace();
                    return createFromStream;
                }
            } catch (IOException e4) {
                IOException iOException = e4;
                createFromStream = null;
                e = iOException;
                e.printStackTrace();
                return createFromStream;
            }
        }

        private View a(Context context, Drawable drawable, String str, OnClickListener onClickListener, OnClickListener onClickListener2) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            float f = displayMetrics.density;
            View relativeLayout = new RelativeLayout(context);
            View imageView = new ImageView(context);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setId(1);
            int i = (int) (14.0f * f);
            i = (int) (18.0f * f);
            int i2 = (int) (6.0f * f);
            int i3 = (int) (18.0f * f);
            LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (60.0f * f), (int) (60.0f * f));
            layoutParams.addRule(9);
            layoutParams.setMargins(0, i, i2, i3);
            relativeLayout.addView(imageView, layoutParams);
            imageView = new TextView(context);
            imageView.setText(str);
            imageView.setTextSize(14.0f);
            imageView.setGravity(3);
            imageView.setIncludeFontPadding(false);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setLines(2);
            imageView.setId(5);
            imageView.setMinWidth((int) (185.0f * f));
            LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams2.addRule(1, 1);
            layoutParams2.addRule(6, 1);
            int i4 = (int) (10.0f * f);
            layoutParams2.setMargins(0, 0, (int) (5.0f * f), 0);
            relativeLayout.addView(imageView, layoutParams2);
            imageView = new View(context);
            imageView.setBackgroundColor(Color.rgb(214, 214, 214));
            imageView.setId(3);
            layoutParams2 = new RelativeLayout.LayoutParams(-2, 2);
            layoutParams2.addRule(3, 1);
            layoutParams2.addRule(5, 1);
            layoutParams2.addRule(7, 5);
            layoutParams2.setMargins(0, 0, 0, (int) (12.0f * f));
            relativeLayout.addView(imageView, layoutParams2);
            imageView = new LinearLayout(context);
            layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams2.addRule(5, 1);
            layoutParams2.addRule(7, 5);
            layoutParams2.addRule(3, 3);
            View button = new Button(context);
            button.setText("\u8df3\u8fc7");
            button.setBackgroundDrawable(a("buttonNegt.png", context));
            button.setTextColor(Color.rgb(36, 97, Header.STRING_6));
            button.setTextSize(20.0f);
            button.setOnClickListener(onClickListener2);
            button.setId(4);
            LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, (int) (45.0f * f));
            layoutParams3.rightMargin = (int) (14.0f * f);
            layoutParams3.leftMargin = (int) (4.0f * f);
            layoutParams3.weight = 1.0f;
            imageView.addView(button, layoutParams3);
            button = new Button(context);
            button.setText("\u786e\u5b9a");
            button.setTextSize(20.0f);
            button.setTextColor(Color.rgb(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
            button.setBackgroundDrawable(a("buttonPost.png", context));
            button.setOnClickListener(onClickListener);
            layoutParams3 = new LinearLayout.LayoutParams(0, (int) (45.0f * f));
            layoutParams3.weight = 1.0f;
            layoutParams3.rightMargin = (int) (4.0f * f);
            imageView.addView(button, layoutParams3);
            relativeLayout.addView(imageView, layoutParams2);
            LayoutParams layoutParams4 = new FrameLayout.LayoutParams((int) (279.0f * f), (int) (163.0f * f));
            relativeLayout.setPadding((int) (14.0f * f), 0, (int) (12.0f * f), (int) (12.0f * f));
            relativeLayout.setLayoutParams(layoutParams4);
            relativeLayout.setBackgroundColor(Color.rgb(247, 251, 247));
            Drawable paintDrawable = new PaintDrawable(Color.rgb(247, 251, 247));
            paintDrawable.setCornerRadius(f * 5.0f);
            relativeLayout.setBackgroundDrawable(paintDrawable);
            return relativeLayout;
        }

        protected void a() {
            HttpUtils.requestAsync(this.b.mToken, this.b.c, "http://appsupport.qq.com/cgi-bin/qzapps/mapp_addapp.cgi", this.b.composeActivityParams(), HttpRequest.METHOD_POST, null);
        }

        public void onError(UiError uiError) {
            if (this.a != null) {
                this.a.onError(uiError);
            }
        }

        public void onCancel() {
            if (this.a != null) {
                this.a.onCancel();
            }
        }
    }

    /* compiled from: ProGuard */
    private class TokenListener implements IUiListener {
        final /* synthetic */ AuthAgent a;
        private final IUiListener b;
        private final boolean c;
        private final Context d;

        public TokenListener(AuthAgent authAgent, Context context, IUiListener iUiListener, boolean z, boolean z2) {
            this.a = authAgent;
            this.d = context;
            this.b = iUiListener;
            this.c = z;
            f.b("openSDK_LOG", "OpenUi, TokenListener()");
        }

        public void onComplete(Object obj) {
            f.b("openSDK_LOG", "OpenUi, TokenListener() onComplete");
            JSONObject jSONObject = (JSONObject) obj;
            try {
                String string = jSONObject.getString(ShareRequestParam.REQ_PARAM_TOKEN);
                String string2 = jSONObject.getString(Constants.PARAM_EXPIRES_IN);
                String string3 = jSONObject.getString(SocialConstants.PARAM_OPEN_ID);
                if (!(string == null || this.a.mToken == null || string3 == null)) {
                    this.a.mToken.setAccessToken(string, string2);
                    this.a.mToken.setOpenId(string3);
                    a.d(this.d, this.a.mToken);
                }
                string = jSONObject.getString(Constants.PARAM_PLATFORM_ID);
                if (string != null) {
                    try {
                        this.d.getSharedPreferences(Constants.PREFERENCE_PF, 0).edit().putString(Constants.PARAM_PLATFORM_ID, string).commit();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        f.b("openSDK_LOG", "OpenUi, TokenListener() onComplete error", e);
                    }
                }
                if (this.c) {
                    CookieSyncManager.getInstance().sync();
                }
            } catch (Throwable e2) {
                e2.printStackTrace();
                f.b("openSDK_LOG", "OpenUi, TokenListener() onComplete error", e2);
            }
            this.b.onComplete(jSONObject);
            this.a.releaseResource();
            f.b();
        }

        public void onError(UiError uiError) {
            f.b("openSDK_LOG", "OpenUi, TokenListener() onError");
            this.b.onError(uiError);
            f.b();
        }

        public void onCancel() {
            f.b("openSDK_LOG", "OpenUi, TokenListener() onCancel");
            this.b.onCancel();
            f.b();
        }
    }

    public AuthAgent(QQToken qQToken) {
        super(qQToken);
    }

    public int doLogin(Activity activity, String str, IUiListener iUiListener) {
        return doLogin(activity, str, iUiListener, false, null);
    }

    public int doLogin(Activity activity, String str, IUiListener iUiListener, boolean z, Fragment fragment) {
        this.b = str;
        this.c = activity;
        this.a = iUiListener;
        if (a(activity, fragment, z)) {
            f.c("openSDK_LOG", "OpenUi, showUi, return Constants.UI_ACTIVITY");
            d.a().a(this.mToken.getOpenId(), this.mToken.getAppId(), Constants.VIA_SSO_LOGIN, Constants.VIA_TO_TYPE_QQ_GROUP, Constants.VIA_SHARE_TYPE_TEXT, Constants.VIA_RESULT_SUCCESS, Constants.VIA_RESULT_SUCCESS, Constants.VIA_RESULT_SUCCESS);
            return 1;
        }
        d.a().a(this.mToken.getOpenId(), this.mToken.getAppId(), Constants.VIA_SSO_LOGIN, Constants.VIA_TO_TYPE_QQ_GROUP, Constants.VIA_SHARE_TYPE_TEXT, Constants.VIA_TO_TYPE_QQ_GROUP, Constants.VIA_RESULT_SUCCESS, Constants.VIA_RESULT_SUCCESS);
        f.d("openSDK_LOG", "startActivity fail show dialog.");
        this.a = new FeedConfirmListener(this, this.a);
        return a(z, this.a);
    }

    public void releaseResource() {
        this.c = null;
        this.a = null;
    }

    private int a(boolean z, IUiListener iUiListener) {
        f.c("openSDK_LOG", "OpenUi, showDialog -- start");
        CookieSyncManager.createInstance(Global.getContext());
        Bundle composeCGIParams = composeCGIParams();
        if (z) {
            composeCGIParams.putString("isadd", Constants.VIA_TO_TYPE_QQ_GROUP);
        }
        composeCGIParams.putString(Constants.PARAM_SCOPE, this.b);
        composeCGIParams.putString(Constants.PARAM_CLIENT_ID, this.mToken.getAppId());
        if (isOEM) {
            composeCGIParams.putString(Constants.PARAM_PLATFORM_ID, "desktop_m_qq-" + installChannel + "-" + AbstractSpiCall.ANDROID_CLIENT_TYPE + "-" + registerChannel + "-" + businessId);
        } else {
            composeCGIParams.putString(Constants.PARAM_PLATFORM_ID, Constants.DEFAULT_PF);
        }
        String str = (System.currentTimeMillis() / 1000) + Table.STRING_DEFAULT_VALUE;
        composeCGIParams.putString("sign", SystemUtils.getAppSignatureMD5(Global.getContext(), str));
        composeCGIParams.putString("time", str);
        composeCGIParams.putString(WBConstants.AUTH_PARAMS_DISPLAY, "mobile");
        composeCGIParams.putString(WBConstants.AUTH_PARAMS_RESPONSE_TYPE, NetWorker.PARAM_KEY_TOKEN);
        composeCGIParams.putString(WBConstants.AUTH_PARAMS_REDIRECT_URL, ServerSetting.DEFAULT_REDIRECT_URI);
        composeCGIParams.putString("cancel_display", Constants.VIA_TO_TYPE_QQ_GROUP);
        composeCGIParams.putString("switch", Constants.VIA_TO_TYPE_QQ_GROUP);
        composeCGIParams.putString("status_userip", Util.getUserIp());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ServerSetting.getInstance().getEnvUrl(Global.getContext(), ServerSetting.DEFAULT_CGI_AUTHORIZE));
        stringBuilder.append(Util.encodeUrl(composeCGIParams));
        String stringBuilder2 = stringBuilder.toString();
        IUiListener tokenListener = new TokenListener(this, Global.getContext(), iUiListener, true, false);
        f.b("openSDK_LOG", "OpenUi, showDialog TDialog");
        ThreadManager.executeOnSubThread(new AnonymousClass1(this, stringBuilder2, tokenListener));
        f.c("openSDK_LOG", "OpenUi, showDialog -- end");
        return 2;
    }

    private boolean a(Activity activity, Fragment fragment, boolean z) {
        f.c("openSDK_LOG", "startActionActivity() -- start");
        Intent targetActivityIntent = getTargetActivityIntent("com.tencent.open.agent.AgentActivity");
        if (targetActivityIntent != null) {
            Bundle composeCGIParams = composeCGIParams();
            if (z) {
                composeCGIParams.putString("isadd", Constants.VIA_TO_TYPE_QQ_GROUP);
            }
            composeCGIParams.putString(Constants.PARAM_SCOPE, this.b);
            composeCGIParams.putString(Constants.PARAM_CLIENT_ID, this.mToken.getAppId());
            if (isOEM) {
                composeCGIParams.putString(Constants.PARAM_PLATFORM_ID, "desktop_m_qq-" + installChannel + "-" + AbstractSpiCall.ANDROID_CLIENT_TYPE + "-" + registerChannel + "-" + businessId);
            } else {
                composeCGIParams.putString(Constants.PARAM_PLATFORM_ID, Constants.DEFAULT_PF);
            }
            composeCGIParams.putString("need_pay", Constants.VIA_TO_TYPE_QQ_GROUP);
            composeCGIParams.putString(Constants.KEY_APP_NAME, SystemUtils.getAppName(Global.getContext()));
            targetActivityIntent.putExtra(Constants.KEY_ACTION, SystemUtils.ACTION_LOGIN);
            targetActivityIntent.putExtra(Constants.KEY_PARAMS, composeCGIParams);
            this.mActivityIntent = targetActivityIntent;
            if (hasActivityForIntent()) {
                this.a = new FeedConfirmListener(this, this.a);
                if (fragment != null) {
                    f.b("AuthAgent", "startAssitActivity fragment");
                    startAssitActivity(fragment, this.a);
                } else {
                    f.b("AuthAgent", "startAssitActivity activity");
                    startAssitActivity(activity, this.a);
                }
                f.c("openSDK_LOG", "startActionActivity() -- end");
                d.a().a(0, "LOGIN_CHECK_SDK", Constants.DEFAULT_UIN, this.mToken.getAppId(), Table.STRING_DEFAULT_VALUE, Long.valueOf(SystemClock.elapsedRealtime()), 0, 1, Table.STRING_DEFAULT_VALUE);
                return true;
            }
        }
        d.a().a(1, "LOGIN_CHECK_SDK", Constants.DEFAULT_UIN, this.mToken.getAppId(), Table.STRING_DEFAULT_VALUE, Long.valueOf(SystemClock.elapsedRealtime()), 0, 1, "startActionActivity fail");
        f.c("openSDK_LOG", "startActionActivity() -- end");
        return false;
    }

    protected void a(IUiListener iUiListener) {
        f.c("openSDK_LOG", "reportDAU() -- start");
        String str = "tencent&sdk&qazxc***14969%%";
        String str2 = "qzone3.4";
        Object accessToken = this.mToken.getAccessToken();
        Object openId = this.mToken.getOpenId();
        Object appId = this.mToken.getAppId();
        Object obj = Table.STRING_DEFAULT_VALUE;
        if (!(TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(openId) || TextUtils.isEmpty(appId))) {
            obj = Util.encrypt(str + accessToken + appId + openId + str2);
        }
        if (TextUtils.isEmpty(obj)) {
            f.e("openSDK_LOG", "reportDAU -- encrytoken is null");
            return;
        }
        Bundle composeCGIParams = composeCGIParams();
        composeCGIParams.putString("encrytoken", obj);
        HttpUtils.requestAsync(this.mToken, Global.getContext(), "https://openmobile.qq.com/user/user_login_statis", composeCGIParams, HttpRequest.METHOD_POST, null);
        f.c("openSDK_LOG", "reportDAU() -- end");
    }

    protected void b(IUiListener iUiListener) {
        Bundle composeCGIParams = composeCGIParams();
        composeCGIParams.putString("reqType", "checkLogin");
        IRequestListener tempRequestListener = new TempRequestListener(new CheckLoginListener(this, iUiListener));
        HttpUtils.requestAsync(this.mToken, Global.getContext(), "https://openmobile.qq.com/v3/user/get_info", composeCGIParams, HttpRequest.METHOD_GET, tempRequestListener);
    }

    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        IUiListener iUiListener;
        ThreadManager.executeOnSubThread(new Runnable() {
            final /* synthetic */ AuthAgent a;

            {
                this.a = r1;
            }

            public void run() {
                Global.saveVersionCode();
            }
        });
        for (ApiTask apiTask : this.mTaskList) {
            if (apiTask.mRequestCode == i) {
                IUiListener iUiListener2 = apiTask.mListener;
                this.mTaskList.remove(apiTask);
                iUiListener = iUiListener2;
                break;
            }
        }
        iUiListener = null;
        if (intent != null) {
            a(intent.getStringExtra(Constants.KEY_RESPONSE));
            if (iUiListener == null) {
                AssistActivity.setResultDataForLogin(activity, intent);
                return;
            }
            if (i2 == -1) {
                BaseApi.handleDataToListener(intent, iUiListener);
            } else {
                f.b("openSDK_LOG", "OpenUi, onActivityResult, Constants.ACTIVITY_CANCEL");
                iUiListener.onCancel();
            }
            releaseResource();
            f.b();
        } else if (iUiListener != null) {
            iUiListener.onCancel();
        }
    }

    private void a(String str) {
        try {
            JSONObject parseJson = Util.parseJson(str);
            Object string = parseJson.getString(ShareRequestParam.REQ_PARAM_TOKEN);
            Object string2 = parseJson.getString(Constants.PARAM_EXPIRES_IN);
            Object string3 = parseJson.getString(SocialConstants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2) && !TextUtils.isEmpty(string3)) {
                this.mToken.setAccessToken(string, string2);
                this.mToken.setOpenId(string3);
            }
        } catch (Exception e) {
        }
    }
}
