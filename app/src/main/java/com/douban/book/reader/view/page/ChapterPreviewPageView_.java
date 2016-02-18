package com.douban.book.reader.view.page;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.FlexibleScrollView;
import com.douban.book.reader.view.ParagraphView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChapterPreviewPageView_ extends ChapterPreviewPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ChapterPreviewPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ChapterPreviewPageView build(Context context) {
        ChapterPreviewPageView_ instance = new ChapterPreviewPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_page_chapter_preview, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mPublishDate = (TextView) hasViews.findViewById(R.id.publish_date);
        this.mBtnPurchaseChapter = (Button) hasViews.findViewById(R.id.btn_purchase_chapter);
        this.mBtnPurchaseWorks = (Button) hasViews.findViewById(R.id.btn_purchase_works);
        this.mBtnDownload = (Button) hasViews.findViewById(R.id.btn_download);
        this.mPurchaseNeeded = (TextView) hasViews.findViewById(R.id.text_status_indicator);
        this.mScrollView = (FlexibleScrollView) hasViews.findViewById(R.id.summary_container);
        this.mSummary = (ParagraphView) hasViews.findViewById(R.id.summary);
        if (this.mBtnPurchaseWorks != null) {
            this.mBtnPurchaseWorks.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterPreviewPageView_.this.onBtnPurchaseWorksClicked();
                }
            });
        }
        if (this.mBtnPurchaseChapter != null) {
            this.mBtnPurchaseChapter.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterPreviewPageView_.this.onBtnPurchaseChapterClicked();
                }
            });
        }
        if (this.mBtnDownload != null) {
            this.mBtnDownload.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterPreviewPageView_.this.onBtnDownloadClicked();
                }
            });
        }
        init();
    }
}
