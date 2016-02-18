package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.douban.book.reader.R;
import com.douban.book.reader.lib.view.IconCheckButton;
import com.douban.book.reader.manager.UserManager_;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareTargetSelectorView_ extends ShareTargetSelectorView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ShareTargetSelectorView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareTargetSelectorView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareTargetSelectorView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ShareTargetSelectorView build(Context context) {
        ShareTargetSelectorView_ instance = new ShareTargetSelectorView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_share_target_selector, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mUserManager = UserManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ShareTargetSelectorView build(Context context, AttributeSet attrs) {
        ShareTargetSelectorView_ instance = new ShareTargetSelectorView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ShareTargetSelectorView build(Context context, AttributeSet attrs, int defStyleAttr) {
        ShareTargetSelectorView_ instance = new ShareTargetSelectorView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mShareToDouban = (IconCheckButton) hasViews.findViewById(R.id.share_to_douban);
        this.mShareToWeibo = (IconCheckButton) hasViews.findViewById(R.id.share_to_weibo);
        if (this.mShareToDouban != null) {
            this.mShareToDouban.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ShareTargetSelectorView_.this.onShareToDoubanClicked();
                }
            });
            this.mShareToDouban.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ShareTargetSelectorView_.this.onShareToDoubanCheckedChange(isChecked);
                }
            });
        }
        if (this.mShareToWeibo != null) {
            this.mShareToWeibo.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ShareTargetSelectorView_.this.onShareToWeiboClicked();
                }
            });
            this.mShareToWeibo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ShareTargetSelectorView_.this.onShareToWeiboCheckedChange(isChecked);
                }
            });
        }
        init();
    }
}
