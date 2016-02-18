package com.douban.book.reader.view.store.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class StoreWorksItemView_ extends StoreWorksItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public StoreWorksItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public StoreWorksItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public StoreWorksItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static StoreWorksItemView build(Context context) {
        StoreWorksItemView_ instance = new StoreWorksItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_store_works_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static StoreWorksItemView build(Context context, AttributeSet attrs) {
        StoreWorksItemView_ instance = new StoreWorksItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static StoreWorksItemView build(Context context, AttributeSet attrs, int defStyle) {
        StoreWorksItemView_ instance = new StoreWorksItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mAuthor = (TextView) hasViews.findViewById(R.id.author);
    }
}
