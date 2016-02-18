package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SimpleNotificationItemView_ extends SimpleNotificationItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public SimpleNotificationItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SimpleNotificationItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SimpleNotificationItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static SimpleNotificationItemView build(Context context) {
        SimpleNotificationItemView_ instance = new SimpleNotificationItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_simple_notification_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static SimpleNotificationItemView build(Context context, AttributeSet attrs) {
        SimpleNotificationItemView_ instance = new SimpleNotificationItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static SimpleNotificationItemView build(Context context, AttributeSet attrs, int defStyle) {
        SimpleNotificationItemView_ instance = new SimpleNotificationItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mContent = (TextView) hasViews.findViewById(R.id.content);
        this.mIconUnRead = (ImageView) hasViews.findViewById(R.id.ic_unread);
        init();
    }
}
