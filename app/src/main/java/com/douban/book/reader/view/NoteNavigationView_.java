package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NoteNavigationView_ extends NoteNavigationView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public NoteNavigationView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public NoteNavigationView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public NoteNavigationView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static NoteNavigationView build(Context context) {
        NoteNavigationView_ instance = new NoteNavigationView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_note_bottom_bar, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static NoteNavigationView build(Context context, AttributeSet attrs) {
        NoteNavigationView_ instance = new NoteNavigationView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static NoteNavigationView build(Context context, AttributeSet attrs, int defStyleAttr) {
        NoteNavigationView_ instance = new NoteNavigationView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnPrev = (TextView) hasViews.findViewById(R.id.note_prev);
        this.mBtnNext = (TextView) hasViews.findViewById(R.id.note_next);
        if (this.mBtnPrev != null) {
            this.mBtnPrev.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NoteNavigationView_.this.showPreviousNote();
                }
            });
        }
        if (this.mBtnNext != null) {
            this.mBtnNext.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NoteNavigationView_.this.showNextNote();
                }
            });
        }
    }
}
