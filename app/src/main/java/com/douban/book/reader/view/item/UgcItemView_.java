package com.douban.book.reader.view.item;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.ParagraphView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UgcItemView_ extends UgcItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public UgcItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static UgcItemView build(Context context) {
        UgcItemView_ instance = new UgcItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.item_ugc, this);
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
        this.mThumbnail = (ImageView) hasViews.findViewById(R.id.thumbnail);
        this.mAbstract = (ParagraphView) hasViews.findViewById(R.id.abstract_text);
        this.mDate = (TextView) hasViews.findViewById(R.id.create_date);
        this.mUuid = (TextView) hasViews.findViewById(R.id.data_uuid);
        init();
    }
}
