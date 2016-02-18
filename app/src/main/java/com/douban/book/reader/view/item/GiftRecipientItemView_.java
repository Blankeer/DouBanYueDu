package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftRecipientItemView_ extends GiftRecipientItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public GiftRecipientItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftRecipientItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftRecipientItemView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftRecipientItemView build(Context context) {
        GiftRecipientItemView_ instance = new GiftRecipientItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_gift_recipient_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static GiftRecipientItemView build(Context context, AttributeSet attrs) {
        GiftRecipientItemView_ instance = new GiftRecipientItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static GiftRecipientItemView build(Context context, AttributeSet attrs, int defStyleAttr) {
        GiftRecipientItemView_ instance = new GiftRecipientItemView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mAvatar = (UserAvatarView) hasViews.findViewById(R.id.avatar);
        this.mUserName = (TextView) hasViews.findViewById(R.id.username);
        this.mReceivedTime = (TextView) hasViews.findViewById(R.id.received_time);
        init();
    }
}
