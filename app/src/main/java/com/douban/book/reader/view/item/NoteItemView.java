package com.douban.book.reader.view.item;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import io.realm.internal.Table;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903121)
public class NoteItemView extends RelativeLayout {
    @ViewById(2131558548)
    ParagraphView mAbstract;
    @ViewById(2131558764)
    TextView mDate;
    @ViewById(2131558765)
    TextView mExtraInfo;
    @ViewById(2131558633)
    ParagraphView mQuotedText;
    @ViewById(2131558763)
    TextView mUuid;

    public NoteItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        this.mAbstract.setFirstLineIndent(Indent.NONE);
        this.mAbstract.setVisibleLineCount(3);
        this.mQuotedText.setFirstLineIndent(Indent.NONE);
        this.mQuotedText.setVisibleLineCount(3);
        this.mQuotedText.setBlockQuote(true);
        this.mQuotedText.setLinkEnabled(false);
    }

    public void setAnnotation(Annotation annotation) {
        this.mAbstract.setEnabled(annotation.isRangeValid());
        ViewUtils.showTextIfNotEmpty(this.mAbstract, annotation.note);
        ViewUtils.showTextIfNotEmpty(this.mQuotedText, annotation.getMarkedText());
        this.mDate.setText(DateUtils.formatDate(annotation.createTime));
        ViewUtils.showTextIfNotEmpty(this.mExtraInfo, getExtraInfo(annotation));
        ViewUtils.showTextIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_NOTE_IDS), this.mUuid, String.format("%s (%s)", new Object[]{annotation.uuid, Integer.valueOf(annotation.id)}));
    }

    private CharSequence getExtraInfo(Annotation annotation) {
        if (annotation.isPrivate()) {
            return new RichText().append((char) Char.SPACE).appendIcon((int) R.drawable.v_private);
        }
        return Table.STRING_DEFAULT_VALUE;
    }
}
