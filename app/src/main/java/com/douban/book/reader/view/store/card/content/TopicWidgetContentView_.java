package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.douban.book.reader.R;
import com.douban.book.reader.view.store.WorksGridView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class TopicWidgetContentView_ extends TopicWidgetContentView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public TopicWidgetContentView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public TopicWidgetContentView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public TopicWidgetContentView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static TopicWidgetContentView build(Context context) {
        TopicWidgetContentView_ instance = new TopicWidgetContentView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_store_topic_widget_content, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static TopicWidgetContentView build(Context context, AttributeSet attrs) {
        TopicWidgetContentView_ instance = new TopicWidgetContentView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static TopicWidgetContentView build(Context context, AttributeSet attrs, int defStyle) {
        TopicWidgetContentView_ instance = new TopicWidgetContentView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnMore = (Button) hasViews.findViewById(R.id.btn_more);
        this.mTopWorks = (WorksGridView) hasViews.findViewById(R.id.top_works);
    }
}
