package com.sina.weibo.sdk.register.mobile;

import android.content.Context;
import android.widget.AbsListView.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sina.weibo.sdk.utils.ResourceManager;

public class SelectCountryItemView extends RelativeLayout {
    private TextView mCountryCode;
    private TextView mCountryName;

    public SelectCountryItemView(Context context, String name, String code) {
        super(context);
        initView(name, code);
    }

    private void initView(String name, String code) {
        setLayoutParams(new LayoutParams(-1, ResourceManager.dp2px(getContext(), 40)));
        this.mCountryName = new TextView(getContext());
        this.mCountryName.setTextSize(16.0f);
        RelativeLayout.LayoutParams mCountryNameLp = new RelativeLayout.LayoutParams(-2, -2);
        mCountryNameLp.leftMargin = ResourceManager.dp2px(getContext(), 15);
        mCountryNameLp.addRule(9);
        mCountryNameLp.addRule(15);
        this.mCountryName.setGravity(16);
        this.mCountryName.setLayoutParams(mCountryNameLp);
        this.mCountryName.setText(name);
        this.mCountryName.setTextColor(-13421773);
        addView(this.mCountryName);
        this.mCountryCode = new TextView(getContext());
        this.mCountryCode.setTextSize(16.0f);
        RelativeLayout.LayoutParams mCountryCodeLp = new RelativeLayout.LayoutParams(-2, -2);
        mCountryCodeLp.addRule(15);
        mCountryCodeLp.addRule(11);
        mCountryCodeLp.rightMargin = ResourceManager.dp2px(getContext(), 40);
        this.mCountryCode.setLayoutParams(mCountryCodeLp);
        this.mCountryCode.setText(code);
        this.mCountryCode.setTextColor(-11502161);
        this.mCountryCode.setGravity(16);
        addView(this.mCountryCode);
        setContent(name, code);
    }

    public void updateContent(String name, String code) {
        setContent(name, code);
    }

    private void setContent(String name, String code) {
        this.mCountryName.setText(name);
        this.mCountryCode.setText(code);
    }
}
