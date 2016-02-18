package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.GiftPackManager_;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftMessageView_ extends GiftMessageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public GiftMessageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftMessageView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftMessageView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftMessageView build(Context context) {
        GiftMessageView_ instance = new GiftMessageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_gift_message, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mGiftPackManager = GiftPackManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static GiftMessageView build(Context context, AttributeSet attrs) {
        GiftMessageView_ instance = new GiftMessageView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static GiftMessageView build(Context context, AttributeSet attrs, int defStyleAttr) {
        GiftMessageView_ instance = new GiftMessageView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mGiveTime = (ParagraphView) hasViews.findViewById(R.id.give_time);
        this.mGiftNote = (ParagraphView) hasViews.findViewById(R.id.gift_note);
        this.mGiver = (ParagraphView) hasViews.findViewById(R.id.giver);
        this.mRecipient = (ParagraphView) hasViews.findViewById(R.id.recipient);
        this.mGiftNoteContainer = (FlexibleScrollView) hasViews.findViewById(R.id.gift_note_container);
        init();
    }
}
