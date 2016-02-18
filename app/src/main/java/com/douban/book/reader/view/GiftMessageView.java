package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import java.util.Date;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903160)
public class GiftMessageView extends LinearLayout {
    private static final String TAG;
    @ViewById(2131558863)
    ParagraphView mGiftNote;
    @ViewById(2131558862)
    FlexibleScrollView mGiftNoteContainer;
    @Bean
    GiftPackManager mGiftPackManager;
    @ViewById(2131558864)
    ParagraphView mGiveTime;
    @ViewById(2131558762)
    ParagraphView mGiver;
    @ViewById(2131558861)
    ParagraphView mRecipient;

    static {
        TAG = GiftMessageView.class.getSimpleName();
    }

    public GiftMessageView(Context context) {
        super(context);
    }

    public GiftMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        setOrientation(1);
        this.mRecipient.setTypeface(Font.SERIF);
        this.mGiver.setTypeface(Font.SERIF);
        this.mGiftNote.setTypeface(Font.SERIF);
        this.mGiveTime.setTypeface(Font.SERIF);
    }

    public GiftMessageView giver(CharSequence giver) {
        ViewUtils.showTextIfNotEmpty(this.mGiver, giver);
        return this;
    }

    public GiftMessageView recipient(CharSequence recipient) {
        ViewUtils.showTextIf(StringUtils.isNotEmpty(recipient), this.mRecipient, RichText.format((int) R.string.text_recipient, recipient));
        return this;
    }

    public GiftMessageView message(CharSequence message) {
        ViewUtils.showTextIfNotEmpty(this.mGiftNote, message);
        return this;
    }

    public GiftMessageView messageDate(Date messageDate) {
        this.mGiveTime.setParagraphText(DateUtils.formatDateWithUnit(messageDate));
        return this;
    }

    public CharSequence getMessage() {
        return this.mGiftNote.getText();
    }

    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(listener);
        this.mGiftNote.setOnClickListener(listener);
    }
}
