package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class PurchaseRecordItemView_ extends PurchaseRecordItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public PurchaseRecordItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public PurchaseRecordItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public PurchaseRecordItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static PurchaseRecordItemView build(Context context) {
        PurchaseRecordItemView_ instance = new PurchaseRecordItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_general_record_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static PurchaseRecordItemView build(Context context, AttributeSet attrs) {
        PurchaseRecordItemView_ instance = new PurchaseRecordItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static PurchaseRecordItemView build(Context context, AttributeSet attrs, int defStyle) {
        PurchaseRecordItemView_ instance = new PurchaseRecordItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mWorksTitle = (TextView) hasViews.findViewById(R.id.works_title);
        this.mTime = (TextView) hasViews.findViewById(R.id.time);
        this.mCount = (TextView) hasViews.findViewById(R.id.count);
        init();
    }
}
