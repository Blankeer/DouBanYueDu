package com.douban.book.reader.view.item;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903123)
public class UgcItemView extends LinearLayout {
    @ViewById(2131558548)
    ParagraphView mAbstract;
    @ViewById(2131558764)
    TextView mDate;
    @ViewById(2131558771)
    ImageView mThumbnail;
    @ViewById(2131558763)
    TextView mUuid;

    public UgcItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        setOrientation(1);
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        this.mAbstract.setFirstLineIndent(Indent.NONE);
        this.mAbstract.setVisibleLineCount(3);
    }

    public void setBookmark(Bookmark bookmark) {
        fillView(bookmark.uuid, bookmark.getThumbnailUri(), bookmark.getAbstract(), DateUtils.formatDate(bookmark.createTime), bookmark.isPositionValid());
    }

    public void setAnnotation(Annotation annotation) {
        fillView(annotation.uuid, null, annotation.getMarkedText(), DateUtils.formatDate(annotation.createTime), annotation.isRangeValid());
    }

    private void fillView(UUID uuid, String thumbnailUri, CharSequence abstractText, String createTime, boolean isPositionValid) {
        if (TextUtils.isEmpty(thumbnailUri)) {
            this.mThumbnail.setVisibility(8);
        } else {
            this.mThumbnail.setVisibility(0);
            ImageLoaderUtils.displayImageSkippingDiscCache(thumbnailUri, this.mThumbnail);
        }
        this.mAbstract.setEnabled(isPositionValid);
        this.mAbstract.setParagraphText(abstractText);
        this.mAbstract.setLinkEnabled(false);
        ViewUtils.showIf(StringUtils.isNotEmpty(abstractText), this.mAbstract);
        this.mDate.setText(createTime);
        ViewUtils.setTopMargin(this.mDate, Res.getDimensionPixelSize(StringUtils.isEmpty(abstractText) ? R.dimen.general_subview_vertical_padding_medium : R.dimen.general_subview_vertical_padding_small));
        ViewUtils.showTextIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_NOTE_IDS), this.mUuid, String.valueOf(uuid));
    }
}
