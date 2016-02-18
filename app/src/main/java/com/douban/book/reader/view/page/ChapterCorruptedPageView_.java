package com.douban.book.reader.view.page;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChapterCorruptedPageView_ extends ChapterCorruptedPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ChapterCorruptedPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ChapterCorruptedPageView build(Context context) {
        ChapterCorruptedPageView_ instance = new ChapterCorruptedPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_page_chapter_corrupted, this);
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
        this.mImage = (ImageView) hasViews.findViewById(com.wnafee.vector.R.id.image);
        this.mBtnDownload = (Button) hasViews.findViewById(R.id.download);
        if (this.mBtnDownload != null) {
            this.mBtnDownload.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterCorruptedPageView_.this.onBtnDownloadClicked();
                }
            });
        }
        init();
    }
}
