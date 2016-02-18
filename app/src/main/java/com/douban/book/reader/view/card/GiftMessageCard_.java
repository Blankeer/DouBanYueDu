package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.R;
import com.douban.book.reader.view.GiftMessageView;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftMessageCard_ extends GiftMessageCard implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public GiftMessageCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftMessageCard build(Context context) {
        GiftMessageCard_ instance = new GiftMessageCard_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public void onViewChanged(HasViews hasViews) {
        this.mAvatarView = (UserAvatarView) hasViews.findViewById(R.id.user_avatar);
        this.mGiftMessageView = (GiftMessageView) hasViews.findViewById(R.id.gift_message);
    }
}
