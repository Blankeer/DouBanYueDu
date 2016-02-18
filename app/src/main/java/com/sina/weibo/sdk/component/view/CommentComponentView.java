package com.sina.weibo.sdk.component.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.cmd.WbAppActivator;
import com.sina.weibo.sdk.component.WeiboSdkBrowser;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.utils.ResourceManager;

public class CommentComponentView extends FrameLayout {
    private static final String ALREADY_COMMENT_EN = "Comment";
    private static final String ALREADY_COMMENT_ZH_CN = "\u5fae\u535a\u70ed\u8bc4";
    private static final String ALREADY_COMMENT_ZH_TW = "\u5fae\u535a\u71b1\u8a55";
    private static final String COMMENT_H5 = "http://widget.weibo.com/distribution/socail_comments_sdk.php";
    private RequestParam mCommentParam;
    private LinearLayout mContentLy;

    public enum Category {
        MOVIE("1001"),
        TRAVEL("1002");
        
        private String mVal;

        private Category(String value) {
            this.mVal = value;
        }

        public String getValue() {
            return this.mVal;
        }
    }

    public static class RequestParam {
        private String mAccessToken;
        private String mAppKey;
        private WeiboAuthListener mAuthlistener;
        private Category mCategory;
        private String mContent;
        private String mTopic;

        private RequestParam() {
        }

        public static RequestParam createRequestParam(String appKey, String token, String commentTopic, String commentContent, Category category, WeiboAuthListener listener) {
            RequestParam param = new RequestParam();
            param.mAppKey = appKey;
            param.mAccessToken = token;
            param.mTopic = commentTopic;
            param.mContent = commentContent;
            param.mCategory = category;
            param.mAuthlistener = listener;
            return param;
        }

        public static RequestParam createRequestParam(String appKey, String commentTopic, String commentContent, Category category, WeiboAuthListener listener) {
            RequestParam param = new RequestParam();
            param.mAppKey = appKey;
            param.mTopic = commentTopic;
            param.mContent = commentContent;
            param.mCategory = category;
            param.mAuthlistener = listener;
            return param;
        }
    }

    public CommentComponentView(Context context) {
        super(context);
        init(context);
    }

    public CommentComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CommentComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContentLy = new LinearLayout(context);
        this.mContentLy.setOrientation(0);
        this.mContentLy.setLayoutParams(new LayoutParams(-2, -2));
        ImageView logoIv = new ImageView(context);
        logoIv.setImageDrawable(ResourceManager.getDrawable(context, "sdk_weibo_logo.png"));
        LinearLayout.LayoutParams logoIvLp = new LinearLayout.LayoutParams(ResourceManager.dp2px(getContext(), 20), ResourceManager.dp2px(getContext(), 20));
        logoIvLp.gravity = 16;
        logoIv.setLayoutParams(logoIvLp);
        TextView commentTv = new TextView(context);
        commentTv.setText(ResourceManager.getString(context, ALREADY_COMMENT_EN, ALREADY_COMMENT_ZH_CN, ALREADY_COMMENT_ZH_TW));
        commentTv.setTextColor(-32256);
        commentTv.setTextSize(2, 15.0f);
        commentTv.setIncludeFontPadding(false);
        LinearLayout.LayoutParams commentTvLp = new LinearLayout.LayoutParams(-2, -2);
        commentTvLp.gravity = 16;
        commentTvLp.leftMargin = ResourceManager.dp2px(getContext(), 4);
        commentTv.setLayoutParams(commentTvLp);
        this.mContentLy.addView(logoIv);
        this.mContentLy.addView(commentTv);
        addView(this.mContentLy);
        commentTv.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CommentComponentView.this.execAttented();
            }
        });
    }

    public void setCommentParam(RequestParam param) {
        this.mCommentParam = param;
    }

    private void execAttented() {
        WbAppActivator.getInstance(getContext(), this.mCommentParam.mAppKey).activateApp();
        WidgetRequestParam req = new WidgetRequestParam(getContext());
        req.setUrl(COMMENT_H5);
        req.setSpecifyTitle(ResourceManager.getString(getContext(), ALREADY_COMMENT_EN, ALREADY_COMMENT_ZH_CN, ALREADY_COMMENT_ZH_TW));
        req.setAppKey(this.mCommentParam.mAppKey);
        req.setCommentTopic(this.mCommentParam.mTopic);
        req.setCommentContent(this.mCommentParam.mContent);
        req.setCommentCategory(this.mCommentParam.mCategory.getValue());
        req.setAuthListener(this.mCommentParam.mAuthlistener);
        req.setToken(this.mCommentParam.mAccessToken);
        Bundle data = req.createRequestParamBundle();
        Intent intent = new Intent(getContext(), WeiboSdkBrowser.class);
        intent.putExtras(data);
        getContext().startActivity(intent);
    }
}
