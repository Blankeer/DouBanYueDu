package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksActionCard_ extends WorksActionCard implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksActionCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksActionCard build(Context context) {
        WorksActionCard_ instance = new WorksActionCard_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.card_content_works_action, this);
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
        this.mBtnRead = (Button) hasViews.findViewById(R.id.btn_read);
        if (this.mBtnRead != null) {
            this.mBtnRead.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    WorksActionCard_.this.onBtnReadClicked();
                }
            });
            this.mBtnRead.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    WorksActionCard_.this.onBtnReadLongClicked();
                    return true;
                }
            });
        }
        init();
    }
}
