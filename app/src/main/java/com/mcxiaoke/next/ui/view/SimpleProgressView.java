package com.mcxiaoke.next.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mcxiaoke.next.ui.R;
import io.realm.internal.Table;

public class SimpleProgressView extends LinearLayout {
    private ProgressBar progressBar;
    private TextView progressTextView;
    private ViewGroup progressView;
    private TextView textView;

    public SimpleProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public SimpleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    @TargetApi(11)
    public SimpleProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.cv_simple_progress, this);
        this.textView = (TextView) findViewById(R.id.spv_text);
        this.progressView = (ViewGroup) findViewById(R.id.spv_progress);
        this.progressBar = (ProgressBar) findViewById(R.id.spv_progress_bar);
        this.progressTextView = (TextView) findViewById(R.id.spv_progress_text);
    }

    public void showProgress() {
        showProgress(false);
    }

    public void showProgress(boolean withText) {
        int i = 0;
        show();
        this.textView.setVisibility(8);
        this.progressView.setVisibility(0);
        TextView textView = this.progressTextView;
        if (!withText) {
            i = 8;
        }
        textView.setVisibility(i);
    }

    private void showText() {
        show();
        this.progressView.setVisibility(8);
        this.textView.setVisibility(0);
    }

    public void showText(int resId) {
        showText(getContext().getString(resId));
    }

    public void showText(CharSequence text) {
        this.textView.setText(text);
        showText();
    }

    public void showEmpty() {
        showText(Table.STRING_DEFAULT_VALUE);
    }

    public void setTextSize(float size) {
        this.textView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        this.textView.setTextSize(unit, size);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextColor(ColorStateList color) {
        this.textView.setTextColor(color);
    }

    public void hide() {
        setVisibility(8);
    }

    public void show() {
        setVisibility(0);
    }
}
