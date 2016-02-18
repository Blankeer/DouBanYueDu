package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.BoxedWorksView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftItemView_ extends GiftItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public GiftItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftItemView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftItemView build(Context context) {
        GiftItemView_ instance = new GiftItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.item_gift, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static GiftItemView build(Context context, AttributeSet attrs) {
        GiftItemView_ instance = new GiftItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static GiftItemView build(Context context, AttributeSet attrs, int defStyleAttr) {
        GiftItemView_ instance = new GiftItemView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBoxedWorksView = (BoxedWorksView) hasViews.findViewById(R.id.gift_box);
        this.mGiver = (TextView) hasViews.findViewById(R.id.giver);
        init();
    }
}
