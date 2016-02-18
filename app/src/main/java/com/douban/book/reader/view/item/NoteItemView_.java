package com.douban.book.reader.view.item;

import android.content.Context;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.view.ParagraphView;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NoteItemView_ extends NoteItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public NoteItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static NoteItemView build(Context context) {
        NoteItemView_ instance = new NoteItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.item_note, this);
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
        this.mUuid = (TextView) hasViews.findViewById(R.id.data_uuid);
        this.mAbstract = (ParagraphView) hasViews.findViewById(R.id.abstract_text);
        this.mQuotedText = (ParagraphView) hasViews.findViewById(R.id.quoted_text);
        this.mDate = (TextView) hasViews.findViewById(R.id.create_date);
        this.mExtraInfo = (TextView) hasViews.findViewById(R.id.note_extra_info);
        init();
    }
}
