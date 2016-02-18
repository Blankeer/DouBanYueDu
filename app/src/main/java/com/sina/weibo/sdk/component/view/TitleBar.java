package com.sina.weibo.sdk.component.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.sina.weibo.sdk.utils.ResourceManager;

public class TitleBar extends RelativeLayout {
    public static final int TITLE_BAR_HEIGHT = 45;
    private ListenerOnTitleBtnClicked mClickListener;
    private TextView mLeftBtn;
    private TextView mTitleText;

    public interface ListenerOnTitleBtnClicked {
        void onLeftBtnClicked();
    }

    public TitleBar(Context context) {
        super(context);
        initViews();
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        this.mLeftBtn = new TextView(getContext());
        this.mLeftBtn.setClickable(true);
        this.mLeftBtn.setTextSize(2, 17.0f);
        this.mLeftBtn.setTextColor(ResourceManager.createColorStateList(-32256, 1728020992));
        LayoutParams leftBtnLp = new LayoutParams(-2, -2);
        leftBtnLp.addRule(5);
        leftBtnLp.addRule(15);
        leftBtnLp.leftMargin = ResourceManager.dp2px(getContext(), 10);
        leftBtnLp.rightMargin = ResourceManager.dp2px(getContext(), 10);
        this.mLeftBtn.setLayoutParams(leftBtnLp);
        this.mLeftBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TitleBar.this.mClickListener != null) {
                    TitleBar.this.mClickListener.onLeftBtnClicked();
                }
            }
        });
        addView(this.mLeftBtn);
        this.mTitleText = new TextView(getContext());
        this.mTitleText.setTextSize(2, 18.0f);
        this.mTitleText.setTextColor(-11382190);
        this.mTitleText.setEllipsize(TruncateAt.END);
        this.mTitleText.setSingleLine(true);
        this.mTitleText.setGravity(17);
        this.mTitleText.setMaxWidth(ResourceManager.dp2px(getContext(), Header.ARRAYLIST_PACKED_LONG));
        LayoutParams titleTextLy = new LayoutParams(-2, -2);
        titleTextLy.addRule(13);
        this.mTitleText.setLayoutParams(titleTextLy);
        addView(this.mTitleText);
        setLayoutParams(new ViewGroup.LayoutParams(-1, ResourceManager.dp2px(getContext(), TITLE_BAR_HEIGHT)));
        setBackgroundDrawable(ResourceManager.getNinePatchDrawable(getContext(), "weibosdk_navigationbar_background.9.png"));
    }

    public void setTitleBarText(String title) {
        this.mTitleText.setText(title);
    }

    public void setLeftBtnText(String left) {
        this.mLeftBtn.setText(left);
    }

    public void setLeftBtnBg(Drawable bgDrawable) {
        this.mLeftBtn.setBackgroundDrawable(bgDrawable);
    }

    public void setTitleBarClickListener(ListenerOnTitleBtnClicked listener) {
        this.mClickListener = listener;
    }
}
