package com.sina.weibo.sdk.component.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sina.weibo.sdk.utils.ResourceManager;

public class AppProgressDialog extends Dialog {
    private TextView info;
    private ProgressBar myBar;

    public AppProgressDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        LinearLayout lly = new LinearLayout(context);
        lly.setLayoutParams(new LayoutParams(ResourceManager.dp2px(context, 100), ResourceManager.dp2px(context, 100)));
        lly.setOrientation(0);
        lly.setGravity(17);
        lly.setBackgroundColor(-1);
        this.myBar = new ProgressBar(context);
        LayoutParams myBarPram = new LayoutParams(-2, -2);
        int dp2px = ResourceManager.dp2px(context, 20);
        myBarPram.bottomMargin = dp2px;
        myBarPram.topMargin = dp2px;
        myBarPram.leftMargin = dp2px;
        this.myBar.setLayoutParams(myBarPram);
        lly.addView(this.myBar);
        this.info = new TextView(context);
        setTitle("\u63d0\u793a");
        this.info.setTextSize(2, 17.0f);
        this.info.setText("\u6b63\u5728\u5904\u7406...");
        this.info.setTextColor(-11382190);
        this.info.setGravity(16);
        LayoutParams minfoPram = new LayoutParams(-2, -2);
        minfoPram.leftMargin = ResourceManager.dp2px(context, 20);
        minfoPram.rightMargin = ResourceManager.dp2px(context, 20);
        this.info.setLayoutParams(minfoPram);
        lly.addView(this.info);
        setContentView(lly);
    }

    public void setMessage(String text) {
        this.info.setText(text);
    }
}
