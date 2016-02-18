package com.sina.weibo.sdk.component.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.cmd.WbAppActivator;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.component.WeiboSdkBrowser;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.component.WidgetRequestParam.WidgetRequestCallback;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.NetUtils;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.ResourceManager;
import com.sina.weibo.sdk.utils.Utility;
import io.fabric.sdk.android.services.network.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class AttentionComponentView extends FrameLayout {
    private static final String ALREADY_ATTEND_EN = "Following";
    private static final String ALREADY_ATTEND_ZH_CN = "\u5df2\u5173\u6ce8";
    private static final String ALREADY_ATTEND_ZH_TW = "\u5df2\u95dc\u6ce8";
    private static final String ATTEND_EN = "Follow";
    private static final String ATTEND_ZH_CN = "\u5173\u6ce8";
    private static final String ATTEND_ZH_TW = "\u95dc\u6ce8";
    private static final String ATTENTION_H5 = "http://widget.weibo.com/relationship/followsdk.php";
    private static final String FRIENDSHIPS_SHOW_URL = "https://api.weibo.com/2/friendships/show.json";
    private static final String TAG;
    private FrameLayout flButton;
    private RequestParam mAttentionParam;
    private TextView mButton;
    private volatile boolean mIsLoadingState;
    private ProgressBar pbLoading;

    public static class RequestParam {
        private String mAccessToken;
        private String mAppKey;
        private String mAttentionScreenName;
        private String mAttentionUid;
        private WeiboAuthListener mAuthlistener;

        private RequestParam() {
        }

        public static RequestParam createRequestParam(String appKey, String token, String attentionUid, String attentionScreenName, WeiboAuthListener listener) {
            RequestParam param = new RequestParam();
            param.mAppKey = appKey;
            param.mAccessToken = token;
            param.mAttentionUid = attentionUid;
            param.mAttentionScreenName = attentionScreenName;
            param.mAuthlistener = listener;
            return param;
        }

        public static RequestParam createRequestParam(String appKey, String attentionUid, String attentionScreenName, WeiboAuthListener listener) {
            RequestParam param = new RequestParam();
            param.mAppKey = appKey;
            param.mAttentionUid = attentionUid;
            param.mAttentionScreenName = attentionScreenName;
            param.mAuthlistener = listener;
            return param;
        }

        private boolean hasAuthoriz() {
            if (TextUtils.isEmpty(this.mAccessToken)) {
                return false;
            }
            return true;
        }
    }

    static {
        TAG = AttentionComponentView.class.getName();
    }

    public AttentionComponentView(Context context) {
        super(context);
        this.mIsLoadingState = false;
        init(context);
    }

    public AttentionComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsLoadingState = false;
        init(context);
    }

    public AttentionComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsLoadingState = false;
        init(context);
    }

    private void init(Context context) {
        Drawable relationShipButtonBg = ResourceManager.createStateListDrawable(context, "common_button_white.9.png", "common_button_white_highlighted.9.png");
        this.flButton = new FrameLayout(context);
        this.flButton.setBackgroundDrawable(relationShipButtonBg);
        this.flButton.setPadding(0, ResourceManager.dp2px(getContext(), 6), ResourceManager.dp2px(getContext(), 2), ResourceManager.dp2px(getContext(), 6));
        this.flButton.setLayoutParams(new LayoutParams(ResourceManager.dp2px(getContext(), 66), -2));
        addView(this.flButton);
        this.mButton = new TextView(getContext());
        this.mButton.setIncludeFontPadding(false);
        this.mButton.setSingleLine(true);
        this.mButton.setTextSize(2, 13.0f);
        LayoutParams buttonLp = new LayoutParams(-2, -2);
        buttonLp.gravity = 17;
        this.mButton.setLayoutParams(buttonLp);
        this.flButton.addView(this.mButton);
        this.pbLoading = new ProgressBar(getContext(), null, 16842873);
        this.pbLoading.setVisibility(8);
        LayoutParams pbLoadingLp = new LayoutParams(-2, -2);
        pbLoadingLp.gravity = 17;
        this.pbLoading.setLayoutParams(pbLoadingLp);
        this.flButton.addView(this.pbLoading);
        this.flButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AttentionComponentView.this.execAttented();
            }
        });
        showFollowButton(false);
    }

    public void setAttentionParam(RequestParam param) {
        this.mAttentionParam = param;
        if (param.hasAuthoriz()) {
            loadAttentionState(param);
        }
    }

    private void startLoading() {
        this.flButton.setEnabled(false);
        this.mButton.setVisibility(8);
        this.pbLoading.setVisibility(0);
    }

    private void stopLoading() {
        this.flButton.setEnabled(true);
        this.mButton.setVisibility(0);
        this.pbLoading.setVisibility(8);
    }

    private void showFollowButton(boolean attention) {
        stopLoading();
        if (attention) {
            this.mButton.setText(ResourceManager.getString(getContext(), ALREADY_ATTEND_EN, ALREADY_ATTEND_ZH_CN, ALREADY_ATTEND_ZH_TW));
            this.mButton.setTextColor(-13421773);
            this.mButton.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(getContext(), "timeline_relationship_icon_attention.png"), null, null, null);
            this.flButton.setEnabled(false);
            return;
        }
        this.mButton.setText(ResourceManager.getString(getContext(), ATTEND_EN, ATTEND_ZH_CN, ATTEND_ZH_TW));
        this.mButton.setTextColor(-32256);
        this.mButton.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(getContext(), "timeline_relationship_icon_addattention.png"), null, null, null);
        this.flButton.setEnabled(true);
    }

    private void loadAttentionState(RequestParam req) {
        if (!this.mIsLoadingState) {
            WbAppActivator.getInstance(getContext(), req.mAppKey).activateApp();
            this.mIsLoadingState = true;
            startLoading();
            WeiboParameters params = new WeiboParameters(req.mAppKey);
            params.put(ShareRequestParam.REQ_PARAM_TOKEN, req.mAccessToken);
            params.put("target_id", req.mAttentionUid);
            params.put("target_screen_name", req.mAttentionScreenName);
            NetUtils.internalHttpRequest(getContext(), FRIENDSHIPS_SHOW_URL, params, HttpRequest.METHOD_GET, new RequestListener() {

                /* renamed from: com.sina.weibo.sdk.component.view.AttentionComponentView.2.1 */
                class AnonymousClass1 implements Runnable {
                    private final /* synthetic */ JSONObject val$target;

                    AnonymousClass1(JSONObject jSONObject) {
                        this.val$target = jSONObject;
                    }

                    public void run() {
                        if (this.val$target != null) {
                            AttentionComponentView.this.showFollowButton(this.val$target.optBoolean("followed_by", false));
                        }
                        AttentionComponentView.this.mIsLoadingState = false;
                    }
                }

                public void onWeiboException(WeiboException e) {
                    LogUtil.d(AttentionComponentView.TAG, "error : " + e.getMessage());
                    AttentionComponentView.this.mIsLoadingState = false;
                }

                public void onComplete(String response) {
                    LogUtil.d(AttentionComponentView.TAG, "json : " + response);
                    try {
                        AttentionComponentView.this.getHandler().post(new AnonymousClass1(new JSONObject(response).optJSONObject("target")));
                    } catch (JSONException e) {
                    }
                }
            });
        }
    }

    private void execAttented() {
        WidgetRequestParam req = new WidgetRequestParam(getContext());
        req.setUrl(ATTENTION_H5);
        req.setSpecifyTitle(ResourceManager.getString(getContext(), ATTEND_EN, ATTEND_ZH_CN, ATTEND_ZH_TW));
        req.setAppKey(this.mAttentionParam.mAppKey);
        req.setAttentionFuid(this.mAttentionParam.mAttentionUid);
        req.setAuthListener(this.mAttentionParam.mAuthlistener);
        req.setToken(this.mAttentionParam.mAccessToken);
        req.setWidgetRequestCallback(new WidgetRequestCallback() {
            public void onWebViewResult(String url) {
                String result = Utility.parseUri(url).getString("result");
                if (!TextUtils.isEmpty(result)) {
                    try {
                        long attented = (long) Integer.parseInt(result);
                        if (attented == 1) {
                            AttentionComponentView.this.showFollowButton(true);
                        } else if (attented == 0) {
                            AttentionComponentView.this.showFollowButton(false);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        });
        Bundle data = req.createRequestParamBundle();
        Intent intent = new Intent(getContext(), WeiboSdkBrowser.class);
        intent.putExtras(data);
        getContext().startActivity(intent);
    }

    private void requestAsync(Context context, String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        NetUtils.internalHttpRequest(context, url, params, httpMethod, listener);
    }
}
