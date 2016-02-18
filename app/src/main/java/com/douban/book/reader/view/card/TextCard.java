package com.douban.book.reader.view.card;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.douban.book.reader.R;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.ParagraphView.OnExpandStatusChangedListener;
import java.util.List;

public class TextCard extends Card<TextCard> {
    private static final int COLLAPSED_LINE_COUNT = 5;
    private boolean mBold;
    private ParagraphView mContentText;
    private ImageView mExpandHandle;
    private boolean mExpandable;
    private Indent mFirstLineIndent;
    private int mGravity;
    private boolean mShowBullet;
    private int mTextColorArrayResId;
    private float mTextSize;

    public TextCard(Context context) {
        super(context);
        this.mFirstLineIndent = Indent.NONE;
        this.mGravity = 3;
        init();
    }

    private void init() {
        super.content((int) R.layout.card_content_text);
        this.mContentText = (ParagraphView) findViewById(R.id.content_text);
        this.mExpandHandle = (ImageView) findViewById(R.id.expand_handle);
        updateTextView();
    }

    public TextCard content(CharSequence content) {
        return content(content, true);
    }

    public TextCard content(CharSequence content, boolean trimContent) {
        if (this.mContentText != null) {
            this.mContentText.setVisibility(0);
            this.mContentText.setParagraphText(content, trimContent);
            updateTextView();
        }
        return this;
    }

    public TextCard content(List<Paragraph> paragraphList) {
        if (this.mContentText != null) {
            this.mContentText.setVisibility(0);
            this.mContentText.setParagraphList(paragraphList);
            updateTextView();
        }
        return this;
    }

    public TextCard firstLineIndent(Indent indent) {
        this.mFirstLineIndent = indent;
        updateTextView();
        return this;
    }

    public TextCard gravity(int gravity) {
        this.mGravity = gravity;
        updateTextView();
        return this;
    }

    public TextCard bold(boolean bold) {
        this.mBold = bold;
        updateTextView();
        return this;
    }

    public TextCard textColorArray(@ArrayRes int resId) {
        this.mTextColorArrayResId = resId;
        updateTextView();
        return this;
    }

    public TextCard textSize(float textSize) {
        this.mTextSize = textSize;
        updateTextView();
        return this;
    }

    public TextCard contentShowBullet() {
        if (this.mContentText != null) {
            this.mContentText.setShowBullet(true);
        }
        return this;
    }

    public TextCard expandable() {
        if (this.mContentText != null) {
            this.mExpandable = true;
            this.mContentText.setOnExpandStatusChangedListener(new OnExpandStatusChangedListener() {
                public void onExpandedStatusChanged(boolean isExpanded) {
                    TextCard.this.mExpandHandle.setImageResource(isExpanded ? R.drawable.ic_collapse : R.drawable.ic_expand);
                }

                public void onExpandNeededChanged(boolean expandNeeded) {
                    ViewUtils.showIf(expandNeeded, TextCard.this.mExpandHandle);
                }
            });
            this.mContentText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TextCard.this.mContentText.toggleExpandedStatus();
                }
            });
            this.mExpandHandle.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TextCard.this.mContentText.toggleExpandedStatus();
                }
            });
            updateTextView();
        }
        return this;
    }

    private void updateTextView() {
        if (this.mContentText != null) {
            this.mContentText.setFirstLineIndent(this.mFirstLineIndent);
            this.mContentText.setGravity(this.mGravity);
            if (this.mExpandable) {
                this.mContentText.setVisibleLineCount(COLLAPSED_LINE_COUNT);
            }
            if (this.mBold) {
                this.mContentText.setTextBold();
            }
            if (this.mTextSize > 0.0f) {
                this.mContentText.setTextSize(this.mTextSize);
            }
            if (this.mTextColorArrayResId > 0) {
                ThemedAttrs.ofView(this.mContentText).append(R.attr.textColorArray, Integer.valueOf(this.mTextColorArrayResId)).updateView();
            }
        }
    }
}
