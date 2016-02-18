package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.realm.internal.Table;

public class ExpandableTextView extends LinearLayout implements OnClickListener {
    ImageButton mButton;
    private Drawable mCollapseDrawable;
    private boolean mCollapsed;
    private Drawable mExpandDrawable;
    private int mMaxCollapsedLines;
    private boolean mRelayout;
    TextView mTv;

    public ExpandableTextView(Context context) {
        super(context);
        this.mRelayout = false;
        this.mCollapsed = true;
        this.mMaxCollapsedLines = 8;
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRelayout = false;
        this.mCollapsed = true;
        this.mMaxCollapsedLines = 8;
        init();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.mRelayout = false;
        this.mCollapsed = true;
        this.mMaxCollapsedLines = 8;
        init();
    }

    void init() {
        this.mMaxCollapsedLines = 8;
        this.mExpandDrawable = null;
        this.mCollapseDrawable = null;
    }

    public void onClick(View v) {
        if (this.mButton.getVisibility() == 0) {
            this.mCollapsed = !this.mCollapsed;
            this.mButton.setImageDrawable(this.mCollapsed ? this.mExpandDrawable : this.mCollapseDrawable);
            this.mTv.setMaxLines(this.mCollapsed ? this.mMaxCollapsedLines : AdvancedShareActionProvider.WEIGHT_MAX);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.mRelayout || getVisibility() == 8) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        this.mRelayout = false;
        this.mButton.setVisibility(8);
        this.mTv.setMaxLines(AdvancedShareActionProvider.WEIGHT_MAX);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mTv.getLineCount() > this.mMaxCollapsedLines) {
            if (this.mCollapsed) {
                this.mTv.setMaxLines(this.mMaxCollapsedLines);
            }
            this.mButton.setVisibility(0);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void findViews() {
        this.mTv = null;
        this.mButton = null;
        this.mTv.setOnClickListener(this);
        this.mButton.setOnClickListener(this);
    }

    public void setText(String text) {
        this.mRelayout = true;
        if (this.mTv == null) {
            findViews();
        }
        String trimmedText = text.trim();
        this.mTv.setText(trimmedText);
        setVisibility(trimmedText.length() == 0 ? 8 : 0);
    }

    public CharSequence getText() {
        if (this.mTv == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return this.mTv.getText();
    }
}
