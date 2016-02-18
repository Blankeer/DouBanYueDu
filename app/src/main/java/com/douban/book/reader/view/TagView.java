package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;

@EView
public class TagView extends TextView {
    private Tag mTag;

    /* renamed from: com.douban.book.reader.view.TagView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Tag val$tag;

        AnonymousClass1(Tag tag) {
            this.val$tag = tag;
        }

        public void onClick(View v) {
            PageOpenHelper.from(TagView.this).open(this.val$tag.uri);
        }
    }

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        int padding = Res.getDimensionPixelSize(R.dimen.general_subview_padding_normal);
        setPadding(padding, padding, padding, padding);
        setGravity(17);
        ViewUtils.setTextAppearance(getContext(), this, R.style.AppWidget_Text_Invert);
        setHeight(Res.getDimensionPixelSize(R.dimen.btn_height));
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.blue)).append(R.attr.textColorArray, Integer.valueOf(R.array.invert_text_color)).updateView();
    }

    public void setEntity(Tag tag) {
        setText(tag.name);
        this.mTag = tag;
        setOnClickListener(new AnonymousClass1(tag));
    }

    public Tag getEntity() {
        return this.mTag;
    }
}
